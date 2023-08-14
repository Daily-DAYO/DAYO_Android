package com.daily.dayo.presentation.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.BuildConfig
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.Event
import com.daily.dayo.common.image.ImageResizeUtil
import com.daily.dayo.common.image.ImageResizeUtil.POST_IMAGE_RESIZE_SIZE
import com.daily.dayo.common.image.ImageResizeUtil.cropCenterBitmap
import com.daily.dayo.common.ListLiveData
import com.daily.dayo.common.Resource
import com.daily.dayo.common.toBitmap
import com.daily.dayo.common.toFile
import com.daily.dayo.data.datasource.remote.folder.CreateFolderInPostRequest
import com.daily.dayo.data.datasource.remote.post.EditPostRequest
import com.daily.dayo.data.mapper.toFolder
import com.daily.dayo.data.mapper.toPost
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.domain.usecase.folder.RequestAllMyFolderListUseCase
import com.daily.dayo.domain.usecase.folder.RequestCreateFolderInPostUseCase
import com.daily.dayo.domain.usecase.post.RequestEditPostUseCase
import com.daily.dayo.domain.usecase.post.RequestPostDetailUseCase
import com.daily.dayo.domain.usecase.post.RequestUploadPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
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
    private val _postContents = MutableLiveData("")
    val postContents get() = _postContents
    private val _postFolderId = MutableLiveData("")
    val postFolderId get() = _postFolderId
    private val _postFolderName = MutableLiveData("")
    val postFolderName get() = _postFolderName
    private val _postImageUriList = ListLiveData<String>() // 갤러리에서 불러온 이미지 리스트
    val postImageUriList get() = _postImageUriList
    private val _postTagList = ListLiveData<String>()
    val postTagList: ListLiveData<String> get() = _postTagList

    // WritePost
    private val _writePostId = MutableLiveData<Event<Int>>()
    val writePostId: LiveData<Event<Int>> get() = _writePostId
    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess: LiveData<Event<Boolean>> get() = _writeSuccess
    private val _writeCurrentPostDetail = MutableLiveData<Post>()
    val writeCurrentPostDetail: LiveData<Post> get() = _writeCurrentPostDetail
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
            return "${DayoApplication.cacheDirPath}/$fileName"
        }

    private fun requestUploadNewPost() = viewModelScope.launch(Dispatchers.IO) {
        _writeSuccess.postValue(Event(false))
        val resizedImages = async {
            postImageUriList.value?.map { item ->
                val postImageBitmap =
                    item.toUri().toBitmap(DayoApplication.applicationContext().contentResolver)
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

        resizedImages.await()?.let {
            requestUploadPostUseCase(
                category = this@WriteViewModel.postCategory.value!!,
                contents = this@WriteViewModel.postContents.value!!,
                files = it.toTypedArray(),
                folderId = this@WriteViewModel.postFolderId.value!!.toInt(),
                tags = this@WriteViewModel.postTagList.value!!.toTypedArray()
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
            EditPostRequest(
                category = this@WriteViewModel.postCategory.value!!,
                contents = this@WriteViewModel.postContents.value!!,
                folderId = this@WriteViewModel.postFolderId.value!!.toInt(),
                hashtags = this@WriteViewModel.postTagList.value!!.toList()
            )
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
                ApiResponse.body?.toPost().let { postDetail ->
                    postDetail?.let {
                        withContext(Dispatchers.Main) {
                            setOriginalPostDetail(postDetail)
                        }
                    }
                }
            }
        }
    }

    private fun setOriginalPostDetail(postDetail: Post) {
        _writeCurrentPostDetail.postValue(postDetail)
        postDetail.postImages?.map { element ->
            addUploadImage(
                Uri.parse("${BuildConfig.BASE_URL}/images/$element")
                    .toString(),
                true
            )
        }

        postDetail.hashtags?.let { _postTagList.addAll(it, false) }
        _postFolderId.postValue(postDetail.folderId.toString())
        _postFolderName.postValue(postDetail.folderName)
    }

    fun requestAllMyFolderList() = viewModelScope.launch(Dispatchers.IO) {
        _folderList.postValue(Resource.loading(null))
        requestAllMyFolderListUseCase()?.let { ApiResponse ->
            _folderList.postValue(
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        Resource.success(ApiResponse.body?.data?.map { it.toFolder() })
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
            requestCreateFolderInPostUseCase(
                CreateFolderInPostRequest(name = name, privacy = privacy)
            ).let { ApiResponse ->
                _folderAddSuccess.postValue(Event(ApiResponse is NetworkResponse.Success))
            }
        }

    fun resetWriteInfoValue() {
        _postId.postValue(0)
        _postContents.postValue("")
        _postFolderId.postValue("")
        _postFolderName.postValue("")
        _postImageUriList.postValue(arrayListOf())
        _postTagList.clear(notify = false)
    }

    fun setPostId(id: Int) {
        _postId.value = id
    }

    fun setPostContents(contents: String) {
        _postContents.value = contents
    }

    fun setPostCategory(category: Category?) {
        _postCategory.value = category
    }

    fun setFolderId(id: String) {
        _postFolderId.value = id
    }

    fun setFolderName(name: String) {
        _postFolderName.value = name
    }

    fun addUploadImage(uriPath: String, notify: Boolean = false) {
        _postImageUriList.add(uriPath, notify)
    }

    fun deleteUploadImage(pos: Int, notify: Boolean = false) {
        _postImageUriList.removeAt(pos, notify)
    }

    fun clearUploadImage() {
        _postImageUriList.clear(notify = true)
    }

    fun addPostTag(tagText: String, notify: Boolean = false) {
        _postTagList.add(tagText, notify)
    }

    fun removePostTag(tagText: String, notify: Boolean = false) {
        _postTagList.remove(tagText, notify)
    }
}