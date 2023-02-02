package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun searchKeyword(keyword: String) = viewModelScope.launch {
        requestSearchKeywordUseCase(keyword = keyword)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _searchTagList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toSearch() })) }
                is NetworkResponse.NetworkError -> { _searchTagList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _searchTagList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _searchTagList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun deleteSearchKeywordRecent(keyword: String) =
        deleteSearchKeywordRecentUseCase(keyword)

    fun clearSearchKeywordRecent() = clearSearchKeywordRecentUseCase()
}