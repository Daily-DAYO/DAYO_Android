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
    var isInitLoadingDAYOPICK = false
    var isInitLoadingNew = false

    lateinit var currentDayoPickCategory : Category
    lateinit var currentNewCategory : Category

    private val _dayoPickPostList = MutableLiveData<Resource<List<Post>>>()
    val dayoPickPostList: LiveData<Resource<List<Post>>> get() = _dayoPickPostList

    private val _newPostList = MutableLiveData<Resource<List<Post>>>()
    val newPostList: LiveData<Resource<List<Post>>> get() = _newPostList

    fun requestDayoPickPostList() = viewModelScope.launch(Dispatchers.IO) {
        if(currentDayoPickCategory == Category.ALL){
            requestHomeDayoPickPostList()
        } else {
            requestHomeDayoPickPostListCategory(currentDayoPickCategory)
        }
    }
    fun requestNewPostList() = viewModelScope.launch(Dispatchers.IO) {
        if(currentNewCategory == Category.ALL){
            requestHomeNewPostList()
        } else {
            requestHomeDayoPickPostListCategory(currentNewCategory)
        }
    }

    fun requestHomeNewPostList() = viewModelScope.launch(Dispatchers.IO) {
        val response = requestNewPostListUseCase()
        if(response.isSuccessful){
            _newPostList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _newPostList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestHomeNewPostListCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestNewPostListCategoryUseCase(category = category)
        if(response.isSuccessful) {
            _newPostList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _newPostList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestHomeDayoPickPostList() = viewModelScope.launch(Dispatchers.IO) {
        _dayoPickPostList.postValue(Resource.loading(null))
        val response = requestHomeDayoPickPostListUseCase()
        if(response.isSuccessful){
            _dayoPickPostList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _dayoPickPostList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestHomeDayoPickPostListCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestDayoPickPostListCategoryUseCase(category = category)
        if(response.isSuccessful) {
            _dayoPickPostList.postValue(Resource.success(response.body()?.data?.map { it.toPost() }))
        } else {
            _dayoPickPostList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestLikePostUseCase(CreateHeartRequest(postId = postId))
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestUnlikePostUseCase(postId = postId)
    }
}