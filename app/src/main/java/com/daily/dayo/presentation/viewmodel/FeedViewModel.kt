package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.data.mapper.toPost
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.usecase.bookmark.RequestBookmarkPostUseCase
import com.daily.dayo.domain.usecase.bookmark.RequestDeleteBookmarkPostUseCase
import com.daily.dayo.domain.usecase.like.RequestLikePostUseCase
import com.daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import com.daily.dayo.domain.usecase.post.RequestFeedListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _feedList = MutableLiveData<Resource<List<Post>>>()
    val feedList: LiveData<Resource<List<Post>>> get() = _feedList

    private val _postLiked = MutableLiveData<Resource<CreateHeartResponse>>()
    val postLiked: LiveData<Resource<CreateHeartResponse>> get() = _postLiked

    private val _postBookmarked = MutableLiveData<Resource<CreateBookmarkResponse>>()
    val postBookmarked: LiveData<Resource<CreateBookmarkResponse>> get() = _postBookmarked

    fun requestFeedList() = viewModelScope.launch {
        _feedList.postValue(Resource.loading(null))
        val response = requestFeedListUseCase()
        if (response.isSuccessful) {
            _feedList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _feedList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch {
        requestLikePostUseCase(CreateHeartRequest(postId = postId)).let {
            if (it.isSuccessful) {
                _postLiked.postValue(Resource.success(it.body()))
            } else {
                _postLiked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        requestUnlikePostUseCase(postId = postId)
    }

    fun requestBookmarkPost(postId: Int) = viewModelScope.launch {
        requestBookmarkPostUseCase(CreateBookmarkRequest(postId = postId)).let {
            if (it.isSuccessful) {
                _postBookmarked.postValue(Resource.success(it.body()))
            } else {
                _postBookmarked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestDeleteBookmarkPost(postId: Int) = viewModelScope.launch {
        requestDeleteBookmarkPostUseCase(postId = postId)
    }
}