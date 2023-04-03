package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.daily.dayo.domain.model.Search
import com.daily.dayo.domain.usecase.search.ClearSearchKeywordRecentUseCase
import com.daily.dayo.domain.usecase.search.DeleteSearchKeywordRecentUseCase
import com.daily.dayo.domain.usecase.search.RequestSearchKeywordRecentUseCase
import com.daily.dayo.domain.usecase.search.RequestSearchKeywordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val deleteSearchKeywordRecentUseCase: DeleteSearchKeywordRecentUseCase,
    private val clearSearchKeywordRecentUseCase: ClearSearchKeywordRecentUseCase,
    private val requestSearchKeywordRecentUseCase: RequestSearchKeywordRecentUseCase,
    private val requestSearchKeywordUseCase: RequestSearchKeywordUseCase
) :
    ViewModel() {
    private val _searchTagList = MutableLiveData<PagingData<Search>>()
    val searchTagList: LiveData<PagingData<Search>> get() = _searchTagList

    fun getSearchKeywordRecent() = requestSearchKeywordRecentUseCase()

    fun searchKeyword(keyword: String) = viewModelScope.launch {
        requestSearchKeywordUseCase(keyword = keyword)
            .cachedIn(viewModelScope)
            .collectLatest { _searchTagList.postValue(it) }
    }

    fun deleteSearchKeywordRecent(keyword: String) =
        deleteSearchKeywordRecentUseCase(keyword)

    fun clearSearchKeywordRecent() = clearSearchKeywordRecentUseCase()
}