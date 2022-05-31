package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.mapper.toPost
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.usecase.like.RequestLikePostUseCase
import com.daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import com.daily.dayo.domain.usecase.post.RequestDayoPickPostListCategoryUseCase
import com.daily.dayo.domain.usecase.post.RequestDayoPickPostListUseCase
import com.daily.dayo.domain.usecase.post.RequestNewPostListCategoryUseCase
import com.daily.dayo.domain.usecase.post.RequestNewPostListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var currentCategory = Category.ALL

    private val _postList = MutableLiveData<Resource<List<Post>>>()
    val postList: LiveData<Resource<List<Post>>> get() = _postList

    fun requestDayoPickPostList() = viewModelScope.launch {
        if(currentCategory == Category.ALL){
            requestHomeDayoPickPostList()
        } else {
            requestHomeDayoPickPostListCategory(currentCategory)
        }
    }

    fun requestHomeNewPostList() = viewModelScope.launch {
        val response = requestNewPostListUseCase()
        if(response.isSuccessful){
            _postList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _postList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestHomeNewPostListCategory(category: Category) = viewModelScope.launch {
        val response = requestNewPostListCategoryUseCase(category = category)
        if(response.isSuccessful) {
            _postList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _postList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestHomeDayoPickPostList() = viewModelScope.launch {
        _postList.postValue(Resource.loading(null))
        val response = requestHomeDayoPickPostListUseCase()
        if(response.isSuccessful){
            _postList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _postList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestHomeDayoPickPostListCategory(category: Category) = viewModelScope.launch {
        val response = requestDayoPickPostListCategoryUseCase(category = category)
        if(response.isSuccessful) {
            _postList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _postList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch {
        val response = requestLikePostUseCase(CreateHeartRequest(postId = postId))
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        val response = requestUnlikePostUseCase(postId = postId)
    }
}