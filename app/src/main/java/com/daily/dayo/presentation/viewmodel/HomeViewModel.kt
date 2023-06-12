package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.mapper.toPost
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.usecase.like.RequestLikePostUseCase
import com.daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import com.daily.dayo.domain.usecase.post.RequestDayoPickPostListCategoryUseCase
import com.daily.dayo.domain.usecase.post.RequestDayoPickPostListUseCase
import com.daily.dayo.domain.usecase.post.RequestNewPostListCategoryUseCase
import com.daily.dayo.domain.usecase.post.RequestNewPostListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val requestNewPostListUseCase: RequestNewPostListUseCase,
    private val requestNewPostListCategoryUseCase: RequestNewPostListCategoryUseCase,
    private val requestHomeDayoPickPostListUseCase: RequestDayoPickPostListUseCase,
    private val requestDayoPickPostListCategoryUseCase: RequestDayoPickPostListCategoryUseCase,
    private val requestLikePostUseCase: RequestLikePostUseCase,
    private val requestUnlikePostUseCase: RequestUnlikePostUseCase
) : ViewModel() {
    var currentDayoPickCategory = Category.ALL
    var currentNewCategory = Category.ALL

    private val _dayoPickPostList = MutableLiveData<Resource<List<Post>>>()
    val dayoPickPostList: LiveData<Resource<List<Post>>> get() = _dayoPickPostList

    private val _newPostList = MutableLiveData<Resource<List<Post>>>()
    val newPostList: LiveData<Resource<List<Post>>> get() = _newPostList

    fun requestDayoPickPostList() = viewModelScope.launch(Dispatchers.IO) {
        if (currentDayoPickCategory == Category.ALL) {
            requestHomeDayoPickPostList()
        } else {
            requestHomeDayoPickPostListCategory(currentDayoPickCategory)
        }
    }
    fun requestNewPostList() = viewModelScope.launch(Dispatchers.IO) {
        if (currentNewCategory == Category.ALL) {
            requestHomeNewPostList()
        } else {
            requestHomeNewPostListCategory(currentNewCategory)
        }
    }

    private fun requestHomeNewPostList() = viewModelScope.launch(Dispatchers.IO) {
        requestNewPostListUseCase()?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _newPostList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toPost() })) }
                is NetworkResponse.NetworkError -> { _newPostList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _newPostList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _newPostList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    private fun requestHomeNewPostListCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        requestNewPostListCategoryUseCase(category = category)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _newPostList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toPost() })) }
                is NetworkResponse.NetworkError -> { _newPostList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _newPostList.postValue(Resource.error(ApiResponse.error.toString(), null))  }
                is NetworkResponse.UnknownError -> { _newPostList.postValue(Resource.error(ApiResponse.throwable.toString(), null))  }
            }
        }
    }

    private fun requestHomeDayoPickPostList() = viewModelScope.launch(Dispatchers.IO) {
        requestHomeDayoPickPostListUseCase()?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _dayoPickPostList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toPost() })) }
                is NetworkResponse.NetworkError -> { _dayoPickPostList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _dayoPickPostList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _dayoPickPostList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    private fun requestHomeDayoPickPostListCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        requestDayoPickPostListCategoryUseCase(category = category)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _dayoPickPostList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toPost() })) }
                is NetworkResponse.NetworkError -> { _dayoPickPostList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _dayoPickPostList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _dayoPickPostList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestLikePostUseCase(CreateHeartRequest(postId = postId))
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestUnlikePostUseCase(postId = postId)
    }

    fun toggleLikeStatusDayoPick(id: Int, isLike: Boolean) {
        _dayoPickPostList.postValue(
            Resource.success(
                _dayoPickPostList.value?.data?.map {
                    if (it.postId == id) {
                        if (isLike) { this.requestLikePost(it.postId)
                        } else { this.requestUnlikePost(it.postId) }
                        it.copy(
                            heart = isLike,
                            heartCount = if (isLike) {
                                it.heartCount + 1
                            } else {
                                if (it.heartCount <= 0) 0 else it.heartCount - 1
                            }
                        )
                    } else {
                        it
                    }
                }
            )
        )
    }
    fun toggleLikeStatusNewPost(id: Int, isLike: Boolean) {
        _newPostList.postValue(
            Resource.success(
                _newPostList.value?.data?.map {
                    if (it.postId == id) {
                        if (isLike) { this.requestLikePost(it.postId)
                        } else { this.requestUnlikePost(it.postId) }
                        it.copy(
                            heart = isLike,
                            heartCount = if (isLike) {
                                it.heartCount + 1
                            } else {
                                if (it.heartCount <= 0) 0 else it.heartCount - 1
                            }
                        )
                    } else {
                        it
                    }
                }
            )
        )
    }
    fun setPostStatus(postId: Int, isLike: Boolean, heartCount: Int, commentCount: Int) {
        _dayoPickPostList.postValue(
            Resource.success(
                _dayoPickPostList.value?.data?.map {
                    if (it.postId == postId) {
                        it.copy(
                            heart = isLike,
                            heartCount = heartCount,
                            commentCount = commentCount
                        )
                    } else {
                        it
                    }
                }
            )
        )
        _newPostList.postValue(
            Resource.success(
                _newPostList.value?.data?.map {
                    if (it.postId == postId) {
                        it.copy(
                            heart = isLike,
                            heartCount = heartCount,
                            commentCount = commentCount
                        )
                    } else {
                        it
                    }
                }
            )
        )
    }
}