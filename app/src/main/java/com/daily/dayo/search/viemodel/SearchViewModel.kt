package com.daily.dayo.search.viemodel

import androidx.lifecycle.ViewModel
import com.daily.dayo.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) : ViewModel() {
    fun getSearchKeywordRecent() = searchRepository.searchKeywordRecentList

    fun searchKeyword(keyword: String) = searchRepository.searchKeyword(keyword)
    fun deleteSearchKeywordRecent(keyword: String) = searchRepository.deleteSearchKeywordRecent(keyword)
    fun clearSearchKeywordRecent() =searchRepository.clearSearchKeywordRecent()
}