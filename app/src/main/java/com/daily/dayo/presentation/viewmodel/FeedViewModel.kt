package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.usecase.bookmark.RequestBookmarkPostUseCase
import com.daily.dayo.domain.usecase.bookmark.RequestDeleteBookmarkPostUseCase
import com.daily.dayo.domain.usecase.like.RequestLikePostUseCase
import com.daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import com.daily.dayo.domain.usecase.post.RequestFeedListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val requestFeedListUseCase: RequestFeedListUseCase,
    private val requestLikePostUseCase: RequestLikePostUseCase,
    private val requestUnlikePostUseCase: RequestUnlikePostUseCase,
    private val requestBookmarkPostUseCase: RequestBookmarkPostUseCase,
    private val requestDeleteBookmarkPostUseCase: RequestDeleteBookmarkPostUseCase
) : ViewModel() {

    private val _feedList = MutableLiveData<PagingData<Post>>()
    val feedList: LiveData<PagingData<Post>> get() = _feedList

    private val _postLiked = MutableLiveData<Resource<CreateHeartResponse>>()
    val postLiked: LiveData<Resource<CreateHeartResponse>> get() = _postLiked

    private val _postBookmarked = MutableLiveData<Resource<CreateBookmarkResponse>>()
    val postBookmarked: LiveData<Resource<CreateBookmarkResponse>> get() = _postBookmarked

    fun requestFeedList() = viewModelScope.launch {
        requestFeedListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _feedList.postValue(it) }
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch {
        requestLikePostUseCase(CreateHeartRequest(postId = postId))?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _postLiked.postValue(Resource.success(ApiResponse.body)) }
                is NetworkResponse.NetworkError -> { _postLiked.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _postLiked.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _postLiked.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        requestUnlikePostUseCase(postId = postId)
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
        requestDeleteBookmarkPostUseCase(postId = postId)
    }
}