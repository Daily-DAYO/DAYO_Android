package daily.dayo.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
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
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.ListLiveData
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.image.ImageResizeUtil.POST_IMAGE_RESIZE_SIZE
import daily.dayo.presentation.common.image.ImageResizeUtil.cropCenterBitmap
import daily.dayo.presentation.common.image.ImageResizeUtil.resizeBitmap
import daily.dayo.presentation.common.toFile
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
    private val _postId = MutableLiveData(0L)
    val postId get() = _postId
    private val _postCategory = MutableLiveData<Category?>(null)
    val postCategory get() = _postCategory
    private val _writeCategory = MutableStateFlow<Pair<Category?, Int>>(Pair(null, -1))
    val writeCategory get() = _writeCategory
    private val _postContents = MutableLiveData("")
    val postContents get() = _postContents
    private val _writeText = MutableStateFlow("")
    val writeText get() = _writeText
    private val _postFolderId = MutableLiveData(0L)
    val postFolderId get() = _postFolderId
    private val _postFolderName = MutableLiveData("")
    val postFolderName get() = _postFolderName
    private val _writeFolderId = MutableStateFlow<Long?>(null)
    val writeFolderId get() = _writeFolderId
    private val _writeFolderName = MutableStateFlow(null as String?)
    val writeFolderName get() = _writeFolderName
    private val _writeImagesUri = MutableStateFlow<List<ImageAsset>>(emptyList())
    val writeImagesUri = _writeImagesUri.asStateFlow()
    private val _postTagList = ListLiveData<String>()
    val postTagList: ListLiveData<String> get() = _postTagList
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
        if (this@WriteViewModel.postId.value != 0L) {
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
            postId = this@WriteViewModel.postId.value!!,
            category = if (writeCategory.value.first != null) writeCategory.value.first!! else this@WriteViewModel.postCategory.value!!,
            contents = this@WriteViewModel.postContents.value!!,
            folderId = if (writeFolderId.value != null) writeFolderId.value!! else this@WriteViewModel.postFolderId.value!!,
            hashtags = if (writeTags.value.isNotEmpty()) writeTags.value else this@WriteViewModel.postTagList.value!!.toList()
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
        _postId.postValue(0)
        _postContents.postValue("")
        _postFolderId.postValue(0)
        _postFolderName.postValue("")
        _writeFolderId.emit(0)
        _writeFolderName.emit("")
        _postTagList.clear(notify = false)
        _writeTags.emit(emptyList())
        _writeImagesUri.emit(emptyList())
    }

    fun setPostId(id: Long) {
        _postId.value = id
    }

    fun setPostContents(contents: String) {
        _postContents.value = contents
    }

    fun setWriteText(text: String) {
        _writeText.value = text
    }

    fun setPostCategory(category: Pair<Category?, Int>) = viewModelScope.launch {
        _postCategory.value = category.first
        _writeCategory.emit(category)
    }

    fun setFolderId(id: Long) = viewModelScope.launch {
        _postFolderId.value = id
        _writeFolderId.emit(id)
    }

    fun setFolderName(name: String) = viewModelScope.launch {
        _postFolderName.value = name
        _writeFolderName.emit(name)
    }

    /**
     * 갤러리에서 가져온 이미지들을 ViewModel에 추가
     * Bitmap을 로드하지 않고 Uri만 ImageAsset 형태로 저장
     */
    fun addOriginalImages(uris: List<Uri>) {
        viewModelScope.launch {
            val newImageAssets = uris.map { ImageAsset(uriString = it.toString()) }
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
                    val inputStream =
                        applicationContext.contentResolver.openInputStream(originalUri)
                            ?: throw IOException("원본 이미지 스트림 열기 실패")

                    // BitmapRegionDecoder 생성 (전체 이미지를 로드하지 않음)
                    val decoder = BitmapRegionDecoder.newInstance(inputStream, false)
                        ?: throw IOException("BitmapRegionDecoder 생성 실패")

                    // 화면의 '미리보기용' 비트맵과 '원본' 비트맵의 크기 비율 계산
                    // ImageCropStateHolder가 가진 비트맵은 이제 sampledBitmap이므로,
                    // 최종 크롭은 원본 파일의 너비를 기준으로 해야 함
                    val scale = decoder.width.toFloat() / stateHolder.imageWidth
                    val cropProps = stateHolder.cropProperties.value

                    // 미리보기 기준 좌표 -> 원본 기준 좌표로 변환
                    val left = (cropProps.cropOffset.x * scale).roundToInt()
                    val top = ((cropProps.cropOffset.y - stateHolder.imageTop) * scale).roundToInt()
                    val size = (cropProps.cropSize * scale).roundToInt()
                    val cropRect = Rect(left, top, left + size, top + size)

                    // 크롭 영역이 원본 이미지 범위를 벗어나지 않도록 보정
                    cropRect.intersect(0, 0, decoder.width, decoder.height)

                    // 4. 필요한 영역만 정확히 디코딩
                    val croppedBitmap = decoder.decodeRegion(cropRect, null)
                    decoder.recycle()
                    inputStream.close()

                    // 크롭된 이미지를 새 캐시 파일로 저장
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
                        this[imageIndex] = ImageAsset(uriString = newUri.toString())
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

    fun updatePostTags(tagTextList: List<String>, notify: Boolean = false) = viewModelScope.launch {
        _postTagList.replaceAll(tagTextList, notify)
        _writeTags.emit(tagTextList)
    }
}