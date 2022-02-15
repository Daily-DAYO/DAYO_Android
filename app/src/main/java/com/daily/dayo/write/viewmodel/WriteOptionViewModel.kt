package com.daily.dayo.write.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.post.model.ResponsePost
import com.daily.dayo.repository.PostRepository
import com.daily.dayo.repository.WriteRepository
import com.daily.dayo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WriteOptionViewModel @Inject constructor(private val writeRepository: WriteRepository, private val postRepository: PostRepository) : ViewModel() {

    private val _writePostId = MutableLiveData<Event<Int>>()
    val writePostId : LiveData<Event<Int>> get() = _writePostId
    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess : LiveData<Event<Boolean>> get() =_writeSuccess

    private val _writeCurrentPostDetail = MutableLiveData<Event<ResponsePost>>()
    val writeCurrentPostDetail: LiveData<Event<ResponsePost>> get() = _writeCurrentPostDetail
    private val _getCurrentPostSuccess = MutableLiveData<Event<Boolean>>()
    val getCurrentPostSuccess : LiveData<Event<Boolean>> get() =_getCurrentPostSuccess

    fun requestUploadPost(postCategory: String, postContents: String, files: Array<File>,
                   postFolderId: Int, memberId: String, postPrivacy: String, postTags: Array<String>) = viewModelScope.launch {
        val response = writeRepository.requestUploadPost(postCategory, postContents, files, postFolderId, memberId, postPrivacy, postTags)
        if(response.isSuccessful) {
            _writePostId.postValue(Event(response.body()?.id) as Event<Int>?)
            _writeSuccess.postValue(Event(true))
        } else {
            _writeSuccess.postValue(Event(false))
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
}