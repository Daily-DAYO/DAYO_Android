package com.daily.dayo.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.BuildConfig
import com.daily.dayo.common.Event
import com.daily.dayo.common.ListLiveData
import com.daily.dayo.common.Resource
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
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
    private val _getCurrentPostSuccess = MutableLiveData<Event<Boolean>>()
    val getCurrentPostSuccess: LiveData<Event<Boolean>> get() = _getCurrentPostSuccess
    private val _writeEditSuccess = MutableLiveData<Event<Boolean>>()
    val writeEditSuccess: LiveData<Event<Boolean>> get() = _writeEditSuccess

    // WriteFolder
    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList
    private val _folderAddSuccess = MutableLiveData<Event<Boolean>>()
    val folderAddAccess: LiveData<Event<Boolean>> get() = _folderAddSuccess

    fun requestUploadPost(files: Array<File>) = viewModelScope.launch {
        _writeSuccess.postValue(Event(false))
        requestUploadPostUseCase(
            category = this@WriteViewModel.postCategory.value!!,
            contents = this@WriteViewModel.postContents.value!!,
            files = files,
            folderId = this@WriteViewModel.postFolderId.value!!.toInt(),
            tags = this@WriteViewModel.postTagList.value!!.toTypedArray()
        ).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    resetWriteInfoValue()
                    _writePostId.postValue(ApiResponse.body?.let { Event(it.id) })
                    _writeSuccess.postValue(Event(true))
                }
                else -> {
                    _writeSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestEditPost() = viewModelScope.launch {
        requestEditPostUseCase(
            postId = this@WriteViewModel.postId.value!!,
            EditPostRequest(
                category = this@WriteViewModel.postCategory.value!!,
                contents = this@WriteViewModel.postContents.value!!,
                folderId = this@WriteViewModel.postFolderId.value!!.toInt(),
                hashtags = this@WriteViewModel.postTagList.value!!.toList()
            )
        ).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    resetWriteInfoValue()
                    _writePostId.postValue(ApiResponse.body?.let { Event(it.postId) })
                    _writeEditSuccess.postValue(Event(true))
                }
                else -> {
                    _writeEditSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestPostDetail(postId: Int) {
        viewModelScope.launch {
            val response = async {
                resetWriteInfoValue()
                requestPostDetailUseCase(postId = postId)
            }
            response.await().let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        ApiResponse.body?.toPost().let { postDetail ->
                            postDetail?.let {
                                setOriginalPostDetail(postDetail)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setOriginalPostDetail(postDetail: Post) {
        _writeCurrentPostDetail.postValue(postDetail)
        postDetail.postImages?.forEach { element ->
            addUploadImage(
                Uri.parse("${BuildConfig.BASE_URL}/images/$element")
                    .toString()
            )
        }

        postDetail.hashtags?.let { _postTagList.addAll(it) }
        _postFolderId.value = postDetail.folderId.toString()
        _postFolderName.value = postDetail.folderName
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        requestAllMyFolderListUseCase()?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _folderList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toFolder() }))
                }
                is NetworkResponse.NetworkError -> {
                    _folderList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }
                is NetworkResponse.ApiError -> {
                    _folderList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }
                is NetworkResponse.UnknownError -> {
                    _folderList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestCreateFolderInPost(name: String, privacy: Privacy) = viewModelScope.launch {
        requestCreateFolderInPostUseCase(
            CreateFolderInPostRequest(
                name = name,
                privacy = privacy
            )
        ).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _folderAddSuccess.postValue(Event(true))
                }
                else -> {
                    _folderAddSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun resetWriteInfoValue() {
        _postId.postValue(0)
        _postContents.value = ""
        _postFolderId.value = ""
        _postFolderName.value = ""
        _postImageUriList.value = arrayListOf()
        _postTagList.clear(notify = true)
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

    fun addUploadImage(uriPath: String) {
        _postImageUriList.add(uriPath)
    }

    fun deleteUploadImage(pos: Int) {
        _postImageUriList.removeAt(pos)
    }

    fun clearUploadImage() {
        _postImageUriList.clear(notify = true)
    }

    fun addPostTag(tagText: String) {
        _postTagList.add(tagText)
    }

    fun removePostTag(tagText: String) {
        _postTagList.remove(tagText)
    }
}