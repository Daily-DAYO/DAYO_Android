package daily.dayo.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.image.ImageResizeUtil
import daily.dayo.presentation.common.image.ImageResizeUtil.POST_IMAGE_RESIZE_SIZE
import daily.dayo.presentation.common.image.ImageResizeUtil.cropCenterBitmap
import daily.dayo.presentation.common.ListLiveData
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.toBitmap
import daily.dayo.presentation.common.toFile
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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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

    // WriteInfo
    private val _postId = MutableLiveData(0)
    val postId get() = _postId
    private val _postCategory = MutableLiveData<Category?>(null)
    val postCategory get() = _postCategory
    private val _writeCategory = MutableStateFlow<Category?>(null)
    val writeCategory get() = _writeCategory
    private val _postContents = MutableLiveData("")
    val postContents get() = _postContents
    private val _postFolderId = MutableLiveData("")
    val postFolderId get() = _postFolderId
    private val _postFolderName = MutableLiveData("")
    val postFolderName get() = _postFolderName
    private val _writeFolderId = MutableStateFlow("")
    val writeFolderId get() = _writeFolderId
    private val _writeFolderName = MutableStateFlow("")
    val writeFolderName get() = _writeFolderName
    private val _postImageUriList = ListLiveData<String>() // 갤러리에서 불러온 이미지 리스트
    val postImageUriList get() = _postImageUriList
    val writeImagesUri get() = _writeImagesUri.asStateFlow()
    private val _writeImagesUri = MutableStateFlow(emptyList<String>())
    private val _postTagList = ListLiveData<String>()
    val postTagList: ListLiveData<String> get() = _postTagList
    private val _writeTags = MutableStateFlow(emptyList<String>())
    val writeTags get() = _writeTags

    // WritePost
    private val _writePostId = MutableLiveData<Event<Int>>()
    val writePostId: LiveData<Event<Int>> get() = _writePostId
    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess: LiveData<Event<Boolean>> get() = _writeSuccess
    private val _writeCurrentPostDetail = MutableLiveData<PostDetail>()
    val writeCurrentPostDetail: LiveData<PostDetail> get() = _writeCurrentPostDetail
    private val _writeEditSuccess = MutableLiveData<Event<Boolean>>()
    val writeEditSuccess: LiveData<Event<Boolean>> get() = _writeEditSuccess

    // WriteFolder
    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList
    private val _folderAddSuccess = MutableLiveData<Event<Boolean>>()
    val folderAddAccess: LiveData<Event<Boolean>> get() = _folderAddSuccess

    fun requestUploadPost() {
        if (this@WriteViewModel.postId.value != 0) {
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

    private fun requestUploadNewPost() = viewModelScope.launch(Dispatchers.IO) {
        _writeSuccess.postValue(Event(false))
        val resizedImages = async {
            if (writeImagesUri.value.isNotEmpty()) {
                writeImagesUri.value.map { item ->
                    val postImageBitmap =
                        item.toUri().toBitmap(applicationContext.contentResolver)
                            ?.cropCenterBitmap()
                    val resizedImageBitmap = postImageBitmap?.let {
                        ImageResizeUtil.resizeBitmap(
                            originalBitmap = it,
                            resizedWidth = POST_IMAGE_RESIZE_SIZE,
                            resizedHeight = POST_IMAGE_RESIZE_SIZE
                        )
                    }
                    resizedImageBitmap.toFile(uploadImagePath)
                }
            } else {
                postImageUriList.value?.map { item ->
                    val postImageBitmap =
                        item.toUri().toBitmap(applicationContext.contentResolver)
                            ?.cropCenterBitmap()
                    val resizedImageBitmap = postImageBitmap?.let {
                        ImageResizeUtil.resizeBitmap(
                            originalBitmap = it,
                            resizedWidth = POST_IMAGE_RESIZE_SIZE,
                            resizedHeight = POST_IMAGE_RESIZE_SIZE
                        )
                    }
                    resizedImageBitmap.toFile(uploadImagePath)
                }
            }
        }

        resizedImages.await()?.let {
            requestUploadPostUseCase(
                category = if (writeCategory.value != null) writeCategory.value!! else this@WriteViewModel.postCategory.value!!,
                contents = this@WriteViewModel.postContents.value!!,
                files = it.toTypedArray(),
                folderId = if (writeFolderId.value.isNotEmpty()) writeFolderId.value.toInt() else this@WriteViewModel.postFolderId.value!!.toInt(),
                tags = if (writeTags.value.isNotEmpty()) writeTags.value.toTypedArray() else this@WriteViewModel.postTagList.value!!.toTypedArray()
            ).let { ApiResponse ->
                _writeSuccess.postValue(Event(ApiResponse is NetworkResponse.Success))
                if (ApiResponse is NetworkResponse.Success) {
                    resetWriteInfoValue()
                    _writePostId.postValue(ApiResponse.body?.let { Event(it.id) })
                }
            }
        }
    }

    private fun requestUploadEditingPost() = viewModelScope.launch(Dispatchers.IO) {
        requestEditPostUseCase(
            postId = this@WriteViewModel.postId.value!!,
            category = if (writeCategory.value != null) writeCategory.value!! else this@WriteViewModel.postCategory.value!!,
            contents = this@WriteViewModel.postContents.value!!,
            folderId = if (writeFolderId.value.isNotEmpty()) writeFolderId.value.toInt() else this@WriteViewModel.postFolderId.value!!.toInt(),
            hashtags = if (writeTags.value.isNotEmpty()) writeTags.value else this@WriteViewModel.postTagList.value!!.toList()
        ).let { ApiResponse ->
            _writeEditSuccess.postValue(Event(ApiResponse is NetworkResponse.Success))
            if (ApiResponse is NetworkResponse.Success) {
                resetWriteInfoValue()
                _writePostId.postValue(ApiResponse.body?.let { Event(it.postId) })
            }
        }
    }

    fun requestPostDetail(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            resetWriteInfoValue()
            requestPostDetailUseCase(postId = postId)
        }.let { ApiResponse ->
            if (ApiResponse is NetworkResponse.Success) {
                ApiResponse.body.let { postDetail ->
                    postDetail?.let {
                        withContext(Dispatchers.Main) {
                            setOriginalPostDetail(postDetail)
                        }
                    }
                }
            }
        }
    }

    private fun setOriginalPostDetail(postDetail: PostDetail) = viewModelScope.launch {
        _writeCurrentPostDetail.postValue(postDetail)
        postDetail.images.map { element ->
            addUploadImage(
                Uri.parse("${BuildConfig.BASE_URL}/images/$element")
                    .toString(),
                true
            )
        }

        postDetail.hashtags.let { _postTagList.addAll(it, false) }
        postDetail.hashtags.let { _writeTags.emit(it) }
        _postFolderId.postValue(postDetail.folderId.toString())
        _postFolderName.postValue(postDetail.folderName)
        _writeFolderId.emit(postDetail.folderId.toString())
        _writeFolderName.emit(postDetail.folderName)
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

    fun requestCreateFolderInPost(name: String, privacy: Privacy) =
        viewModelScope.launch(Dispatchers.IO) {
            requestCreateFolderInPostUseCase(name = name, privacy = privacy).let { ApiResponse ->
                _folderAddSuccess.postValue(Event(ApiResponse is NetworkResponse.Success))
            }
        }

    fun resetWriteInfoValue() = viewModelScope.launch {
        _postId.postValue(0)
        _postContents.postValue("")
        _postFolderId.postValue("")
        _postFolderName.postValue("")
        _writeFolderId.emit("")
        _writeFolderName.emit("")
        _postImageUriList.postValue(arrayListOf())
        _postTagList.clear(notify = false)
        _writeTags.emit(emptyList())
        viewModelScope.launch {
            _writeImagesUri.emit(emptyList())
        }
    }

    fun setPostId(id: Int) {
        _postId.value = id
    }

    fun setPostContents(contents: String) {
        _postContents.value = contents
    }

    fun setPostCategory(category: Category?) = viewModelScope.launch {
        _postCategory.value = category
        _writeCategory.emit(category)
    }

    fun setFolderId(id: String) = viewModelScope.launch {
        _postFolderId.value = id
        _writeFolderId.emit(id)
    }

    fun setFolderName(name: String) = viewModelScope.launch {
        _postFolderName.value = name
        _writeFolderName.emit(name)
    }

    fun addUploadImage(uriPath: String, notify: Boolean = false) {
        _postImageUriList.add(uriPath, notify)
        _writeImagesUri.getAndUpdate { it + uriPath }
    }

    fun deleteUploadImage(pos: Int, notify: Boolean = false) {
        _postImageUriList.removeAt(pos, notify)
        _writeImagesUri.getAndUpdate { it.filterIndexed { index, _ -> index != pos } }
    }

    fun clearUploadImage() {
        _postImageUriList.clear(notify = true)
        viewModelScope.launch { _writeImagesUri.emit(emptyList()) }
    }

    fun addPostTag(tagText: String, notify: Boolean = false) = viewModelScope.launch {
        _postTagList.add(tagText, notify)
        _writeTags.emit(
            writeTags.value.toMutableList().apply {
                add(tagText)
            }
        )
    }

    fun removePostTag(tagText: String, notify: Boolean = false) = viewModelScope.launch {
        _postTagList.remove(tagText, notify)
        _writeTags.emit(
            writeTags.value.toMutableList().apply {
                remove(tagText)
            }
        )
    }
}