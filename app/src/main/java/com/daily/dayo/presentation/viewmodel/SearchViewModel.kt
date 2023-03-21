package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toSearch
import com.daily.dayo.domain.model.NetworkResponse
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

    fun searchKeyword(keyword: String) = requestSearchKeywordUseCase(keyword = keyword).cachedIn(viewModelScope)

    fun deleteSearchKeywordRecent(keyword: String) =
        deleteSearchKeywordRecentUseCase(keyword)

    fun clearSearchKeywordRecent() = clearSearchKeywordRecentUseCase()
}