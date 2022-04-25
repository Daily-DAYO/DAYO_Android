package com.daily.dayo.search.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.SearchRepository
import com.daily.dayo.search.model.ResponseSearchTag
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    ViewModel() {
    private val _searchTagList = MutableLiveData<Resource<ResponseSearchTag>>()
    val searchTagList: LiveData<Resource<ResponseSearchTag>> get() = _searchTagList

    fun getSearchKeywordRecent() = searchRepository.searchKeywordRecentList

    fun searchKeyword(keyword: String) = viewModelScope.launch {
        searchRepository.searchKeyword(keyword).let {
            if (it.isSuccessful) {
                _searchTagList.postValue(Resource.success(it.body()))
            } else {
                _searchTagList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun deleteSearchKeywordRecent(keyword: String) =
        searchRepository.deleteSearchKeywordRecent(keyword)

    fun clearSearchKeywordRecent() = searchRepository.clearSearchKeywordRecent()
}