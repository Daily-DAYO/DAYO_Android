package com.daily.dayo.post.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.post.model.RequestCreatePostComment
import com.daily.dayo.post.model.ResponsePost
import com.daily.dayo.post.model.ResponsePostComment
import com.daily.dayo.repository.PostRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val postRepository: PostRepository): ViewModel() {
    private val _postDetail = MutableLiveData<Resource<ResponsePost>>()
    val postDetail: LiveData<Resource<ResponsePost>> get() = _postDetail

    private val _requestCreatePostComment = MutableSharedFlow<Boolean>()
    val requestCreatePostComment = _requestCreatePostComment.asSharedFlow()

    private val _postComment = MutableLiveData<Resource<ResponsePostComment>>()
    val postComment: LiveData<Resource<ResponsePostComment>> get() = _postComment

    fun requestPostDetail(postId: Int) = viewModelScope.launch {
        _postDetail.postValue(Resource.loading(null))
        postRepository.requestPostDetail(postId).let {
            if (it.isSuccessful){
                _postDetail.postValue(Resource.success(it.body()))
            } else {
                _postDetail.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestPostComment(postId: Int) = viewModelScope.launch {
        _postComment.postValue(Resource.loading(null))
        postRepository.requestPostComment(postId).let {
            if (it.isSuccessful){
                _requestCreatePostComment.emit(true)
                _postComment.postValue(Resource.success(it.body()))
            } else {
                _requestCreatePostComment.emit(false)
                _postComment.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestCreatePostComment(request: RequestCreatePostComment) = viewModelScope.launch {
        postRepository.requestCreatePostComment(request)
    }
}