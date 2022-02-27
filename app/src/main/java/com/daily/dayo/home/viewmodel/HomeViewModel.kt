package com.daily.dayo.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.R
import com.daily.dayo.home.model.ResponseHomePost
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.post.model.ResponseLikePost
import com.daily.dayo.repository.HomeRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {
    private val _postList = MutableLiveData<Resource<ResponseHomePost>>()
    val postList: LiveData<Resource<ResponseHomePost>> get() = _postList

    private val _postliked = MutableLiveData<Resource<ResponseLikePost>>()
    val postLiked: LiveData<Resource<ResponseLikePost>> get() = _postliked

    var currentCategory = "ALL"

    fun requestNewPostList() = viewModelScope.launch {
        if(currentCategory == "ALL"){
            requestHomeNewPostList()
        } else if(!currentCategory.isNullOrEmpty()) {
            requestHomeNewPostListCategory(currentCategory)
        } else {
            // ERROR
        }
    }
    fun requestDayoPickPostList() = viewModelScope.launch {
        if(currentCategory == "ALL"){
            requestHomeDayoPickPostList()
        } else if(!currentCategory.isNullOrEmpty()) {
            requestHomeDayoPickPostListCategory(currentCategory)
        } else {
            // ERROR
        }
    }

    fun requestHomeNewPostList() = viewModelScope.launch {
        _postList.postValue(Resource.loading(null))
        homeRepository.requestNewPostList().let {
            if(it.isSuccessful){
                _postList.postValue(Resource.success(it.body()))
            } else {
                _postList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
    fun requestHomeNewPostListCategory(category: String) = viewModelScope.launch {
        homeRepository.requestNewPostListCategory(category).let {
            if(it.isSuccessful) {
                _postList.postValue(Resource.success(it.body()))
            } else {
                _postList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestHomeDayoPickPostList() = viewModelScope.launch {
        _postList.postValue(Resource.loading(null))
        homeRepository.requestDayoPickPostList().let {
            if(it.isSuccessful){
                _postList.postValue(Resource.success(it.body()))
            } else {
                _postList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
    fun requestHomeDayoPickPostListCategory(category: String) = viewModelScope.launch {
        homeRepository.requestDayoPickPostListCategory(category).let {
            if(it.isSuccessful) {
                _postList.postValue(Resource.success(it.body()))
            } else {
                _postList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestLikePost(request: RequestLikePost) = viewModelScope.launch {
        homeRepository.requestLikePost(request).let {
            if(it.isSuccessful) {
                _postliked.postValue(Resource.success(it.body()))
            } else {
                _postliked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        homeRepository.requestUnlikePost(postId)
    }
}