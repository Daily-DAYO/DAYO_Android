package com.daily.dayo.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.home.model.Post
import com.daily.dayo.home.model.ResponseHomePost
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

    init {
        requestHomePostList()
    }

    private fun requestHomePostList() = viewModelScope.launch {
        _postList.postValue(Resource.loading(null))
        homeRepository.requestPostList().let {
            if(it.isSuccessful){
                _postList.postValue(Resource.success(it.body()))
            } else {
                _postList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}