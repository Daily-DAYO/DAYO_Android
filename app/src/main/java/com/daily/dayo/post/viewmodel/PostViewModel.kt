package com.daily.dayo.post.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.post.model.*
import com.daily.dayo.repository.PostRepository
import com.daily.dayo.util.Event
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

    private val _postliked = MutableLiveData<Resource<ResponseLikePost>>()
    val postLiked: LiveData<Resource<ResponseLikePost>> get() = _postliked

    private val _postBookmarked = MutableLiveData<Resource<ResponseBookmarkPost>>()
    val postBookmarked: LiveData<Resource<ResponseBookmarkPost>> get() = _postBookmarked

    private val _requestCreatePostComment = MutableSharedFlow<Boolean>()
    val requestCreatePostComment = _requestCreatePostComment.asSharedFlow()

    private val _postCommentDeleteSuccess = MutableLiveData<Event<Boolean>>()
    val postCommentDeleteSuccess get() = _postCommentDeleteSuccess

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

    fun requestDeletePost(postId: Int) = viewModelScope.launch {
        postRepository.requestDeletePost(postId)
    }

    fun requestLikePost(request: RequestLikePost) = viewModelScope.launch {
        postRepository.requestLikePost(request).let {
            if(it.isSuccessful) {
                _postliked.postValue(Resource.success(it.body()))
            } else {
                _postliked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        postRepository.requestUnlikePost(postId)
    }

    fun requestBookmarkPost(request: RequestBookmarkPost) = viewModelScope.launch {
        postRepository.requestBookmarkPost(request).let {
            if(it.isSuccessful) {
                _postBookmarked.postValue(Resource.success(it.body()))
            } else {
                _postBookmarked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestDeleteBookmarkPost(postId: Int) = viewModelScope.launch {
        postRepository.requestDeleteBookmarkPost(postId)
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

    fun requestDeletePostComment(commentId: Int) = viewModelScope.launch {
        postRepository.requestDeletePostComment(commentId).let {
            if(it.isSuccessful) {
                _postCommentDeleteSuccess.postValue(Event(true))
            } else {
                _postCommentDeleteSuccess.postValue(Event(false))
            }
        }
    }
}