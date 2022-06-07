package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toSearch
import com.daily.dayo.domain.model.Search
import com.daily.dayo.domain.usecase.search.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val deleteSearchKeywordRecentUseCase: DeleteSearchKeywordRecentUseCase,
    private val clearSearchKeywordRecentUseCase: ClearSearchKeywordRecentUseCase,
    private val requestSearchKeywordRecentUseCase: RequestSearchKeywordRecentUseCase,
    private val requestSearchKeywordUseCase: RequestSearchKeywordUseCase,
    private val requestSearchTagUseCase: RequestSearchTagUseCase
) :
    ViewModel() {
    private val _searchTagList = MutableLiveData<Resource<List<Search>>>()
    val searchTagList: LiveData<Resource<List<Search>>> get() = _searchTagList

    fun getSearchKeywordRecent() = requestSearchKeywordRecentUseCase()

    fun searchKeyword(keyword: String) = viewModelScope.launch {
        val response = requestSearchKeywordUseCase(keyword = keyword)
        if (response.isSuccessful) {
            _searchTagList.postValue(Resource.success(response.body()?.data?.map { it.toSearch() }))
        } else {
            _searchTagList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun deleteSearchKeywordRecent(keyword: String) =
        deleteSearchKeywordRecentUseCase(keyword)

    fun clearSearchKeywordRecent() = clearSearchKeywordRecentUseCase()
}