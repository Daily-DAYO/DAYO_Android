package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.data.datasource.remote.comment.CreateCommentRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.data.mapper.toComment
import com.daily.dayo.data.mapper.toPost
import com.daily.dayo.domain.model.Comment
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.usecase.block.RequestBlockMemberUseCase
import com.daily.dayo.domain.usecase.bookmark.RequestBookmarkPostUseCase
import com.daily.dayo.domain.usecase.bookmark.RequestDeleteBookmarkPostUseCase
import com.daily.dayo.domain.usecase.comment.RequestCreatePostCommentUseCase
import com.daily.dayo.domain.usecase.comment.RequestDeletePostCommentUseCase
import com.daily.dayo.domain.usecase.comment.RequestPostCommentUseCase
import com.daily.dayo.domain.usecase.like.RequestLikePostUseCase
import com.daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import com.daily.dayo.domain.usecase.post.RequestDeletePostUseCase
import com.daily.dayo.domain.usecase.post.RequestPostDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val requestPostDetailUseCase: RequestPostDetailUseCase,
    private val requestDeletePostUseCase: RequestDeletePostUseCase,
    private val requestLikePostUseCase: RequestLikePostUseCase,
    private val requestUnlikePostUseCase: RequestUnlikePostUseCase,
    private val requestBookmarkPostUseCase: RequestBookmarkPostUseCase,
    private val requestDeleteBookmarkPostUseCase: RequestDeleteBookmarkPostUseCase,
    private val requestPostCommentUseCase: RequestPostCommentUseCase,
    private val requestCreatePostCommentUseCase: RequestCreatePostCommentUseCase,
    private val requestDeletePostCommentUseCase: RequestDeletePostCommentUseCase,
    private val requestBlockMemberUseCase: RequestBlockMemberUseCase
): ViewModel() {

    private val _postDetail = MutableLiveData<Resource<Post>>()
    val postDetail: LiveData<Resource<Post>> get() = _postDetail

    private val _postLiked = MutableLiveData<Resource<CreateHeartResponse>>()
    val postLiked: LiveData<Resource<CreateHeartResponse>> get() = _postLiked

    private val _postBookmarked = MutableLiveData<Resource<CreateBookmarkResponse>>()
    val postBookmarked: LiveData<Resource<CreateBookmarkResponse>> get() = _postBookmarked

    private val _postCommentCreateSuccess = MutableLiveData<Event<Boolean>>()
    val postCommentCreateSuccess get() = _postCommentCreateSuccess

    private val _postCommentDeleteSuccess = MutableLiveData<Event<Boolean>>()
    val postCommentDeleteSuccess get() = _postCommentDeleteSuccess

    private val _postComment = MutableLiveData<Resource<List<Comment>>>()
    val postComment: LiveData<Resource<List<Comment>>> get() = _postComment

    private val _blockSuccess = MutableLiveData<Event<Boolean>>()
    val blockSuccess: LiveData<Event<Boolean>> get() = _blockSuccess

    fun requestPostDetail(postId: Int) = viewModelScope.launch {
        _postDetail.postValue(Resource.loading(null))
        requestPostDetailUseCase(postId).let {
            if (it.isSuccessful){
                _postDetail.postValue(Resource.success(it.body()?.toPost()))
            } else {
                _postDetail.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestDeletePost(postId: Int) = viewModelScope.launch {
        requestDeletePostUseCase(postId)
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch {
        requestLikePostUseCase(CreateHeartRequest(postId = postId)).let {
            if(it.isSuccessful) {
                _postLiked.postValue(Resource.success(it.body()))
            } else {
                _postLiked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        requestUnlikePostUseCase(postId)
    }

    fun requestBookmarkPost(postId: Int) = viewModelScope.launch {
        requestBookmarkPostUseCase(CreateBookmarkRequest(postId = postId)).let {
            if(it.isSuccessful) {
                _postBookmarked.postValue(Resource.success(it.body()))
            } else {
                _postBookmarked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestDeleteBookmarkPost(postId: Int) = viewModelScope.launch {
        requestDeleteBookmarkPostUseCase(postId)
    }

    fun requestPostComment(postId: Int) = viewModelScope.launch {
        _postComment.postValue(Resource.loading(null))
        val response = requestPostCommentUseCase(postId)
        if (response.isSuccessful){
            _postComment.postValue(Resource.success(response.body()?.data?.map { it.toComment() }))
        } else {
            _postComment.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestCreatePostComment(contents: String, postId: Int) = viewModelScope.launch {
        requestCreatePostCommentUseCase(CreateCommentRequest(contents = contents, postId = postId)).let {
            if(it.isSuccessful){
                _postCommentCreateSuccess.postValue(Event(true))
            } else {
                _postCommentCreateSuccess.postValue(Event(false))
            }
        }
    }

    fun requestDeletePostComment(commentId: Int) = viewModelScope.launch {
        requestDeletePostCommentUseCase(commentId).let {
            if(it.isSuccessful) {
                _postCommentDeleteSuccess.postValue(Event(true))
            } else {
                _postCommentDeleteSuccess.postValue(Event(false))
            }
        }
    }

    fun requestBlockMember(memberId: String) = viewModelScope.launch {
        if (requestBlockMemberUseCase(memberId).isSuccessful) {
            _blockSuccess.postValue(Event(true))
        } else {
            _blockSuccess.postValue(Event(false))
        }
    }
}