package com.daily.dayo.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.home.model.Post
import com.daily.dayo.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {
    private val _postList = MutableStateFlow<List<Post>?>(null)
    val postList = _postList.asStateFlow()
    init {
        requestHomePostList()
    }

    private fun requestHomePostList() {
        viewModelScope.launch {
            homeRepository.requestPostList().collect { postList ->
                postList?.let {
                    _postList.emit(postList)
                }
            }
        }
    }
}