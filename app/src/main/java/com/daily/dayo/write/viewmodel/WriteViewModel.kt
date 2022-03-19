package com.daily.dayo.write.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.post.model.ResponsePost
import com.daily.dayo.profile.model.RequestCreateFolderInPost
import com.daily.dayo.profile.model.ResponseAllMyFolderList
import com.daily.dayo.repository.FolderRepository
import com.daily.dayo.repository.PostRepository
import com.daily.dayo.repository.WriteRepository
import com.daily.dayo.util.Event
import com.daily.dayo.util.ListLiveData
import com.daily.dayo.util.Resource
import com.daily.dayo.write.model.RequestEditWrite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val writeRepository: WriteRepository,
    private val postRepository: PostRepository,
    private val folderRepository: FolderRepository
) : ViewModel()  {

    // Show Dialog
    val showWriteOptionDialog = MutableLiveData<Event<Boolean>>()

    // WriteInfo
    var postId = MutableLiveData<Int>(0)
    var postCategory = MutableLiveData<String>("")
    var postContents = MutableLiveData<String>("")
    var postFolderId = MutableLiveData<String>("")
    var postFolderName = MutableLiveData<String>("")
    var postImageUriList = ListLiveData<String>() // 갤러리에서 불러온 이미지 리스트
    var postTagList = ListLiveData<String>()

    // WritePost
    private val _writePostId = MutableLiveData<Event<Int>>()
    val writePostId : LiveData<Event<Int>> get() = _writePostId
    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess : LiveData<Event<Boolean>> get() =_writeSuccess
    private val _writeCurrentPostDetail = MutableLiveData<Event<ResponsePost>>()
    val writeCurrentPostDetail: LiveData<Event<ResponsePost>> get() = _writeCurrentPostDetail
    private val _getCurrentPostSuccess = MutableLiveData<Event<Boolean>>()
    val getCurrentPostSuccess : LiveData<Event<Boolean>> get() =_getCurrentPostSuccess
    private val _writeEditSuccess = MutableLiveData<Event<Boolean>>()
    val writeEditSuccess : LiveData<Event<Boolean>> get() =_writeEditSuccess

    // WriteFolder
    private val _folderList = MutableLiveData<Resource<ResponseAllMyFolderList>>()
    val folderList : LiveData<Resource<ResponseAllMyFolderList>> get() = _folderList

    fun requestUploadPost(postCategory: String, postContents: String, files: Array<File>, postFolderId: Int, postTags: Array<String>)
            = viewModelScope.launch {
        val response = writeRepository.requestUploadPost(postCategory, postContents, files, postFolderId, postTags)
        if(response.isSuccessful) {
            _writePostId.postValue(Event(response.body()?.id) as Event<Int>?)
            _writeSuccess.postValue(Event(true))
        } else {
            _writeSuccess.postValue(Event(false))
        }
    }

    fun requestEditPost(postId: Int, postCategory: String, postContents: String, postFolderId: Int, postTags: Array<String>)
            = viewModelScope.launch {
        val response = writeRepository.requestEditPost(postId,
            RequestEditWrite(postCategory, postContents, postFolderId, postTags.toList())
        )
        if(response.isSuccessful) {
            _writePostId.postValue(Event(response.body()?.postId) as Event<Int>)
            _writeEditSuccess.postValue(Event(true))
        } else {
            _writeEditSuccess.postValue(Event(false))
        }
    }

    fun requestPostDetail(postId: Int) = viewModelScope.launch {
        val response = postRepository.requestPostDetail(postId)
        if(response.isSuccessful) {
            _writeCurrentPostDetail.postValue(Event(response.body()) as Event<ResponsePost>)
            _getCurrentPostSuccess.postValue(Event(true))
        } else {
            _getCurrentPostSuccess.postValue(Event(false))
        }
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        folderRepository.requestAllMyFolderList().let {
            if(it.isSuccessful){
                _folderList.postValue(Resource.success(it.body()))
            } else{
                _folderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestCreateFolderInPost(name:String, privacy:String) = viewModelScope.launch {
        folderRepository.requestCreateFolderInPost(RequestCreateFolderInPost(name,privacy))
    }

    fun setInitWriteInfoValue(){
        postId = MutableLiveData<Int>(0)
        postCategory = MutableLiveData<String>("")
        postContents = MutableLiveData<String>("")
        postFolderId = MutableLiveData<String>("")
        postFolderName = MutableLiveData<String>("")
        postImageUriList = ListLiveData<String>()
        postTagList = ListLiveData<String>()
    }

}