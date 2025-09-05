package daily.dayo.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.Folder
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.PostDetail
import daily.dayo.domain.model.Privacy
import daily.dayo.domain.usecase.folder.RequestAllMyFolderListUseCase
import daily.dayo.domain.usecase.folder.RequestCreateFolderInPostUseCase
import daily.dayo.domain.usecase.post.RequestEditPostUseCase
import daily.dayo.domain.usecase.post.RequestPostDetailUseCase
import daily.dayo.domain.usecase.post.RequestUploadPostUseCase
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.image.ImageResizeUtil.POST_IMAGE_RESIZE_SIZE
import daily.dayo.presentation.common.image.ImageResizeUtil.cropCenterBitmap
import daily.dayo.presentation.common.image.ImageResizeUtil.resizeBitmap
import daily.dayo.presentation.common.image.applyExif
import daily.dayo.presentation.common.image.readExifInfo
import daily.dayo.presentation.common.toFile
import daily.dayo.presentation.screen.home.CategoryMenu
import daily.dayo.presentation.screen.write.ImageAsset
import daily.dayo.presentation.screen.write.ImageCropStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WriteViewModel @Inject constructor(
    // There isn't really a leak here in the context constructor, it is just the lint check doesn't know that is the application context
    @ApplicationContext
    private val applicationContext: Context,
    private val requestUploadPostUseCase: RequestUploadPostUseCase,
    private val requestEditPostUseCase: RequestEditPostUseCase,
    private val requestPostDetailUseCase: RequestPostDetailUseCase,
    private val requestAllMyFolderListUseCase: RequestAllMyFolderListUseCase,
    private val requestCreateFolderInPostUseCase: RequestCreateFolderInPostUseCase
) : ViewModel() {

    // Show Dialog
    val showWriteOptionDialog = MutableLiveData<Event<Boolean>>()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    // WriteInfo
    private val _postId: MutableLiveData<Long?> = MutableLiveData(null)
    val postId get() = _postId
    private val _writeCategory = MutableStateFlow<Pair<Category?, Int>>(Pair(null, -1))
    val writeCategory get() = _writeCategory
    private val _writeText = MutableStateFlow("")
    val writeText get() = _writeText
    private val _writeFolderId = MutableStateFlow<Long?>(null)
    val writeFolderId get() = _writeFolderId
    private val _writeFolderName = MutableStateFlow(null as String?)
    val writeFolderName get() = _writeFolderName
    private val _writeImagesUri = MutableStateFlow<List<ImageAsset>>(emptyList())
    val writeImagesUri = _writeImagesUri.asStateFlow()
    private val _postImages = MutableStateFlow<List<String>>(emptyList())
    val postImages = _postImages.asStateFlow()
    private val _writeTags = MutableStateFlow(emptyList<String>())
    val writeTags get() = _writeTags

    // WritePost
    private val _writePostId = MutableLiveData<Event<Long>>()
    val writePostId: LiveData<Event<Long>> get() = _writePostId
    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess: LiveData<Event<Boolean>> get() = _writeSuccess
    private val _uploadSuccess: MutableStateFlow<Status?> = MutableStateFlow(null)
    val uploadSuccess get() = _uploadSuccess
    private val _writeCurrentPostDetail = MutableLiveData<PostDetail>()
    val writeCurrentPostDetail: LiveData<PostDetail> get() = _writeCurrentPostDetail
    private val _writeEditSuccess = MutableLiveData<Event<Boolean>>()
    val writeEditSuccess: LiveData<Event<Boolean>> get() = _writeEditSuccess

    // WriteFolder
    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList
    private val _folderAddSuccess = MutableLiveData<Event<Boolean>>()
    val folderAddAccess: LiveData<Event<Boolean>> get() = _folderAddSuccess
    private val _writeFolderAddSuccess = MutableStateFlow(Event(false))
    val writeFolderAddSuccess get() = _writeFolderAddSuccess

    fun requestUploadPost() {
        if (this@WriteViewModel.postId.value != null) {
            requestUploadEditingPost()
        } else {
            requestUploadNewPost()
        }
    }

    private val uploadImagePath: String
        get() {
            val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss-SSS", Locale.KOREA)
            // URI를 통해 불러온 이미지를 임시로 파일로 저장할 경로로 앱 내부 캐시 디렉토리로 설정,
            // 파일 이름은 불러온 시간 사용
            val fileName =
                imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() + ".jpg"
            return "${applicationContext.cacheDir}/$fileName"
        }

    /**
     * 새 게시글 업로드
     * 업로드 시점에만 Uri로부터 Bitmap을 로드하여 사용 (OOM 방지)
     */
    private fun requestUploadNewPost() = viewModelScope.launch(Dispatchers.IO) {
        _uploadSuccess.emit(Status.LOADING)
        val imageFiles = _writeImagesUri.value.mapNotNull { imageAsset ->
            // 각 Uri로부터 비트맵을 열고, 리사이즈하고, 파일로 변환
            applicationContext.contentResolver.openInputStream(Uri.parse(imageAsset.uriString))
                ?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val resizedBitmap = resizeBitmap(
                        bitmap.cropCenterBitmap(),
                        POST_IMAGE_RESIZE_SIZE,
                        POST_IMAGE_RESIZE_SIZE
                    )
                    val file = resizedBitmap.toFile(uploadImagePath)
                    bitmap.recycle()
                    resizedBitmap.recycle()
                    file
                }
        }

        if (imageFiles.isEmpty() && _writeImagesUri.value.isNotEmpty()) {
            _errorEvent.emit("이미지를 파일로 변환하는 데 실패했습니다.")
            _uploadSuccess.emit(Status.ERROR)
            return@launch
        }

        requestUploadPostUseCase(
            category = writeCategory.value.first!!,
            contents = writeText.value,
            files = imageFiles.toTypedArray(),
            folderId = writeFolderId.value ?: 0L,
            tags = if (writeTags.value.isNotEmpty()) writeTags.value.toTypedArray() else emptyArray()
        ).let { apiResponse ->
            when (apiResponse) {
                is NetworkResponse.Success -> {
                    _uploadSuccess.emit(Status.SUCCESS)
                }

                else -> {
                    _uploadSuccess.emit(Status.ERROR)
                }
            }
            if (apiResponse is NetworkResponse.Success) {
                _writePostId.postValue(apiResponse.body?.let { Event(it.id) })
            }
        }
    }

    private fun requestUploadEditingPost() = viewModelScope.launch(Dispatchers.IO) {
        requestEditPostUseCase(
            postId = postId.value!!,
            category = writeCategory.value.first!!,
            contents = writeText.value,
            folderId = writeFolderId.value!!,
            hashtags = writeTags.value.ifEmpty { emptyList() }
        ).let { ApiResponse ->
            _writeEditSuccess.postValue(Event(ApiResponse is NetworkResponse.Success))
            if (ApiResponse is NetworkResponse.Success) {
                _writePostId.postValue(ApiResponse.body?.let { Event(it.postId) })
            }
        }
    }

    fun requestAllMyFolderList() = viewModelScope.launch(Dispatchers.IO) {
        _folderList.postValue(Resource.loading(null))
        requestAllMyFolderListUseCase()?.let { ApiResponse ->
            _folderList.postValue(
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        Resource.success(ApiResponse.body?.data)
                    }

                    is NetworkResponse.NetworkError -> {
                        Resource.error(ApiResponse.exception.toString(), null)
                    }

                    is NetworkResponse.ApiError -> {
                        Resource.error(ApiResponse.error.toString(), null)
                    }

                    is NetworkResponse.UnknownError -> {
                        Resource.error(ApiResponse.throwable.toString(), null)
                    }
                }
            )
        }
    }

    fun requestCreateFolderInPost(
        name: String,
        description: String,
        privacy: Privacy
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            requestCreateFolderInPostUseCase(
                name = name,
                description = description,
                privacy = privacy
            ).let { ApiResponse ->
                _folderAddSuccess.postValue(Event(ApiResponse is NetworkResponse.Success))
                _writeFolderAddSuccess.emit(Event(ApiResponse is NetworkResponse.Success))
            }
        }

    /**
     * 상태 초기화
     */
    fun resetWriteInfoValue() = viewModelScope.launch {
        _postId.postValue(null)
        _writeFolderId.emit(0)
        _writeFolderName.emit("")
        _writeTags.emit(emptyList())
        _writeImagesUri.emit(emptyList())
    }

    fun requestPostDetail(postId: Long, categoryMenus: List<CategoryMenu>) {
        viewModelScope.launch {
            requestPostDetailUseCase(postId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        response.body?.run {
                            setPostId(postId)
                            setPostCategory(
                                categoryMenus
                                    .withIndex()
                                    .firstOrNull { it.value.category == category }
                                    ?.let { it.value.category to it.index }
                                    ?: Pair(null, -1)
                            )
                            setWriteText(contents)
                            setFolderId(folderId)
                            setFolderName(folderName)
                            updatePostTags(hashtags)
                            setPostEditImages(images)
                        }
                    }

                    else -> {
                        // TODO 정보를 불러오지 못했을 경우 처리
                    }
                }
            }
        }
    }

    private fun setPostId(id: Long) {
        _postId.value = id
    }

    fun setWriteText(text: String) {
        _writeText.value = text
    }

    fun setPostCategory(category: Pair<Category?, Int>) = viewModelScope.launch {
        _writeCategory.emit(category)
    }

    fun setFolderId(id: Long) = viewModelScope.launch {
        _writeFolderId.emit(id)
    }

    fun setFolderName(name: String) = viewModelScope.launch {
        _writeFolderName.emit(name)
    }

    private fun setPostEditImages(images: List<String>) {
        viewModelScope.launch {
            _postImages.emit(images)
        }
    }

    /**
     * 갤러리에서 가져온 이미지들을 ViewModel에 추가
     * Bitmap을 로드하지 않고 Uri와 EXIF 정보를 ImageAsset 형태로 저장
     */
    fun addOriginalImages(uris: List<Uri>) {
        viewModelScope.launch {
            val newImageAssets = withContext(Dispatchers.IO) {
                uris.map { uri ->
                    val exifInfo = applicationContext.contentResolver.readExifInfo(uri)
                    ImageAsset(uriString = uri.toString(), exifInfo = exifInfo)
                }
            }
            _writeImagesUri.emit(newImageAssets)
        }
    }

    /**
     * 이미지를 편집하고, 완료되면 리스트의 해당 ImageAsset을 새 정보로 교체
     */
    fun cropImageAndUpdate(imageIndex: Int, stateHolder: ImageCropStateHolder) {
        val imageAsset = _writeImagesUri.value.getOrNull(imageIndex) ?: return

        viewModelScope.launch {
            try {
                val newUri = withContext(Dispatchers.IO) {
                    val originalUri = Uri.parse(imageAsset.uriString)

                    // 전체 이미지를 로드하고 EXIF 정보를 적용
                    val originalBitmap = applicationContext.contentResolver.openInputStream(originalUri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    } ?: throw IOException("원본 이미지 로드 실패")

                    val exifInfo = stateHolder.exifInfo

                    // EXIF 정보가 있으면 적용하여 올바른 방향으로 회전
                    val rotatedBitmap = if (exifInfo != null) {
                        originalBitmap.applyExif(exifInfo)
                    } else {
                        originalBitmap
                    }

                    // 원본 비트맵이 다르면 리사이클
                    if (rotatedBitmap != originalBitmap) {
                        originalBitmap.recycle()
                    }

                    val cropProps = stateHolder.cropProperties.value

                    // 미리보기 비트맵과 실제 로드된 비트맵 간의 스케일 계산
                    val scaleX = rotatedBitmap.width.toFloat() / stateHolder.imageWidth
                    val scaleY = rotatedBitmap.height.toFloat() / stateHolder.imageHeight

                    // 크롭 좌표를 실제 비트맵 크기에 맞게 변환
                    val left = (cropProps.cropOffset.x * scaleX).roundToInt()
                    val top = ((cropProps.cropOffset.y - stateHolder.imageTop) * scaleY).roundToInt()
                    val size = (cropProps.cropSize * scaleX).roundToInt()

                    // 크롭 영역이 비트맵 범위를 벗어나지 않도록 보정
                    val finalLeft = left.coerceIn(0, rotatedBitmap.width)
                    val finalTop = top.coerceIn(0, rotatedBitmap.height)
                    val finalRight = (left + size).coerceIn(0, rotatedBitmap.width)
                    val finalBottom = (top + size).coerceIn(0, rotatedBitmap.height)
                    val finalWidth = finalRight - finalLeft
                    val finalHeight = finalBottom - finalTop

                    // 크롭된 비트맵 생성
                    val croppedBitmap = Bitmap.createBitmap(
                        rotatedBitmap,
                        finalLeft,
                        finalTop,
                        finalWidth,
                        finalHeight
                    )

                    // 원본 회전된 비트맵 리사이클
                    rotatedBitmap.recycle()

                    // 크롭된 이미지를 새 캐시 파일로 저장 (EXIF 정보는 제거됨)
                    val cacheFile = File(
                        applicationContext.cacheDir,
                        "cropped_${System.currentTimeMillis()}.jpg"
                    )
                    FileOutputStream(cacheFile).use { out ->
                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                    croppedBitmap.recycle()
                    cacheFile.toUri()
                }

                _writeImagesUri.getAndUpdate { currentList ->
                    currentList.toMutableList().apply {
                        // 편집된 이미지는 EXIF 정보가 제거되므로 null로 설정
                        this[imageIndex] = ImageAsset(uriString = newUri.toString(), exifInfo = null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorEvent.emit("이미지 처리에 실패했습니다. 다시 시도해 주세요.")
            }
        }
    }

    /**
     * 지정된 인덱스의 이미지 제거
     */
    fun removeUploadImage(pos: Int) {
        viewModelScope.launch {
            _writeImagesUri.getAndUpdate { currentList ->
                currentList.filterIndexed { index, _ -> index != pos }
            }
        }
    }

    fun clearUploadImage() {
        viewModelScope.launch {
            _writeImagesUri.emit(emptyList())
        }
    }

    fun updatePostTags(tagTextList: List<String>) = viewModelScope.launch {
        _writeTags.emit(tagTextList)
    }
}