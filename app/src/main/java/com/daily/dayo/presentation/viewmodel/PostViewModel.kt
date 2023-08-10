package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
import com.daily.dayo.domain.model.LikeUser
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.usecase.block.RequestBlockMemberUseCase
import com.daily.dayo.domain.usecase.bookmark.RequestBookmarkPostUseCase
import com.daily.dayo.domain.usecase.bookmark.RequestDeleteBookmarkPostUseCase
import com.daily.dayo.domain.usecase.comment.RequestCreatePostCommentUseCase
import com.daily.dayo.domain.usecase.comment.RequestDeletePostCommentUseCase
import com.daily.dayo.domain.usecase.comment.RequestPostCommentUseCase
import com.daily.dayo.domain.usecase.like.RequestLikePostUseCase
import com.daily.dayo.domain.usecase.like.RequestPostLikeUsersUseCase
import com.daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import com.daily.dayo.domain.usecase.post.RequestDeletePostUseCase
import com.daily.dayo.domain.usecase.post.RequestPostDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val requestBlockMemberUseCase: RequestBlockMemberUseCase,
    private val requestPostLikeUsersUseCase: RequestPostLikeUsersUseCase
) : ViewModel() {

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

    private val _postLikeUsers = MutableStateFlow<PagingData<LikeUser>>(PagingData.empty())
    val postLikeUsers = _postLikeUsers.asStateFlow()

    fun cleanUpPostDetail() {
        _postDetail.postValue(Resource.loading(null))
        _postComment.postValue(Resource.loading(null))
    }

    fun requestPostDetail(postId: Int) = viewModelScope.launch {
        _postDetail.postValue(Resource.loading(null))
        requestPostDetailUseCase(postId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _postDetail.postValue(Resource.success(ApiResponse.body?.toPost())) }
                is NetworkResponse.NetworkError -> { _postDetail.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _postDetail.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _postDetail.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestDeletePost(postId: Int) = viewModelScope.launch {
        requestDeletePostUseCase(postId)
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch {
        requestLikePostUseCase(CreateHeartRequest(postId = postId))?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postDetail.postValue(
                        Resource.success(
                            _postDetail.value?.data?.apply {
                                heart = true
                                heartCount = ApiResponse.body?.allCount?: 0
                            }
                        )
                    )
                }
                is NetworkResponse.NetworkError -> { _postLiked.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _postLiked.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _postLiked.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        requestUnlikePostUseCase(postId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postDetail.postValue(
                        Resource.success(
                            _postDetail.value?.data?.apply {
                                heart = false
                                heartCount = ApiResponse.body?.allCount?: 0
                            }
                        )
                    )
                }
                is NetworkResponse.NetworkError -> {}
                is NetworkResponse.ApiError -> {}
                is NetworkResponse.UnknownError -> {}
            }

        }
    }

    fun requestBookmarkPost(postId: Int) = viewModelScope.launch {
        requestBookmarkPostUseCase(CreateBookmarkRequest(postId = postId)).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _postBookmarked.postValue(Resource.success(ApiResponse.body)) }
                is NetworkResponse.NetworkError -> { _postBookmarked.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _postBookmarked.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _postBookmarked.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestDeleteBookmarkPost(postId: Int) = viewModelScope.launch {
        requestDeleteBookmarkPostUseCase(postId)
    }

    fun requestPostComment(postId: Int) = viewModelScope.launch {
        _postComment.postValue(Resource.loading(null))
        requestPostCommentUseCase(postId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _postComment.postValue(Resource.success(ApiResponse.body?.data?.map { it.toComment() })) }
                is NetworkResponse.NetworkError -> { _postComment.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _postComment.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _postComment.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestCreatePostComment(contents: String, postId: Int) = viewModelScope.launch {
        requestCreatePostCommentUseCase(CreateCommentRequest(contents = contents, postId = postId))?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _postCommentCreateSuccess.postValue(Event(true)) }
                else -> { _postCommentCreateSuccess.postValue(Event(false)) }
            }
        }
    }

    fun requestDeletePostComment(commentId: Int) = viewModelScope.launch {
        requestDeletePostCommentUseCase(commentId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _postCommentDeleteSuccess.postValue(Event(true)) }
                else -> { _postCommentDeleteSuccess.postValue(Event(false)) }
            }
        }
    }

    fun requestBlockMember(memberId: String) = viewModelScope.launch {
        requestBlockMemberUseCase(memberId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _blockSuccess.postValue(Event(true)) }
                else -> { _blockSuccess.postValue(Event(false)) }
            }
        }
    }

    fun requestPostLikeUsers(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        requestPostLikeUsersUseCase(postId = postId)
            .cachedIn(viewModelScope)
            .collectLatest { _postLikeUsers.emit(it) }
    }
}