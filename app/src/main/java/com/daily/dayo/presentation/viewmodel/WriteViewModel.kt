package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.ListLiveData
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.folder.CreateFolderInPostRequest
import com.daily.dayo.data.datasource.remote.post.EditPostRequest
import com.daily.dayo.data.mapper.toFolder
import com.daily.dayo.data.mapper.toPost
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.domain.usecase.folder.RequestAllMyFolderListUseCase
import com.daily.dayo.domain.usecase.folder.RequestCreateFolderInPostUseCase
import com.daily.dayo.domain.usecase.post.RequestEditPostUseCase
import com.daily.dayo.domain.usecase.post.RequestPostDetailUseCase
import com.daily.dayo.domain.usecase.post.RequestUploadPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    var postId = MutableLiveData(0)
    var postCategory = MutableLiveData<Category>(null)
    var postContents = MutableLiveData("")
    var postFolderId = MutableLiveData("")
    var postFolderName = MutableLiveData("")
    var postImageUriList = ListLiveData<String>() // 갤러리에서 불러온 이미지 리스트
    var postTagList = ListLiveData<String>()

    // WritePost
    private val _writePostId = MutableLiveData<Event<Int>>()
    val writePostId: LiveData<Event<Int>> get() = _writePostId
    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess: LiveData<Event<Boolean>> get() = _writeSuccess
    private val _writeCurrentPostDetail = MutableLiveData<Event<Post>>()
    val writeCurrentPostDetail: LiveData<Event<Post>> get() = _writeCurrentPostDetail
    private val _getCurrentPostSuccess = MutableLiveData<Event<Boolean>>()
    val getCurrentPostSuccess: LiveData<Event<Boolean>> get() = _getCurrentPostSuccess
    private val _writeEditSuccess = MutableLiveData<Event<Boolean>>()
    val writeEditSuccess: LiveData<Event<Boolean>> get() = _writeEditSuccess

    // WriteFolder
    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList
    private val _folderAddSuccess = MutableLiveData<Event<Boolean>>()
    val folderAddAccess: LiveData<Event<Boolean>> get() = _folderAddSuccess

    fun requestUploadPost(
        postCategory: Category,
        postContents: String,
        files: Array<File>,
        postFolderId: Int,
        postTags: Array<String>
    ) = viewModelScope.launch {
        _writeSuccess.postValue(Event(false))
        val response = requestUploadPostUseCase(
            category = postCategory,
            contents = postContents,
            files = files,
            folderId = postFolderId,
            tags = postTags
        )
        if (response.isSuccessful) {
            _writePostId.postValue(response.body()?.let { Event(it.id) })
            _writeSuccess.postValue(Event(true))
        } else {
            _writeSuccess.postValue(Event(false))
        }
    }

    fun requestEditPost(
        postId: Int,
        postCategory: Category,
        postContents: String,
        postFolderId: Int,
        postTags: Array<String>
    ) = viewModelScope.launch {
        val response = requestEditPostUseCase(
            postId = postId,
            EditPostRequest(
                category = postCategory,
                contents = postContents,
                folderId = postFolderId,
                hashtags = postTags.toList()
            )
        )
        if (response.isSuccessful) {
            _writePostId.postValue(response.body()?.let { Event(it.postId) })
            _writeEditSuccess.postValue(Event(true))
        } else {
            _writeEditSuccess.postValue(Event(false))
        }
    }

    fun requestPostDetail(postId: Int) = viewModelScope.launch {
        val response = requestPostDetailUseCase(postId = postId)
        if (response.isSuccessful) {
            _writeCurrentPostDetail.postValue(
                Event(
                    response.body()?.toPost()
                ) as Event<Post>?
            )
            _getCurrentPostSuccess.postValue(Event(true))
        } else {
            _getCurrentPostSuccess.postValue(Event(false))
        }
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        val response = requestAllMyFolderListUseCase()
        if (response.isSuccessful) {
            _folderList.postValue(Resource.success(response.body()?.data?.map { it.toFolder() }))
        } else {
            _folderList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestCreateFolderInPost(name: String, privacy: Privacy) = viewModelScope.launch {
        requestCreateFolderInPostUseCase(
            CreateFolderInPostRequest(
                name = name,
                privacy = privacy
            )
        ).let {
            if (it.isSuccessful) {
                _folderAddSuccess.postValue(Event(true))
            } else {
                _folderAddSuccess.postValue(Event(false))
            }
        }
    }

    fun setInitWriteInfoValue() {
        postId = MutableLiveData<Int>(0)
        postCategory = MutableLiveData<Category>(null)
        postContents = MutableLiveData<String>("")
        postFolderId = MutableLiveData<String>("")
        postFolderName = MutableLiveData<String>("")
        postImageUriList = ListLiveData()
        postTagList = ListLiveData()
    }
}