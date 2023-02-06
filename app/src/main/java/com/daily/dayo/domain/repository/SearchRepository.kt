package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.search.SearchResultResponse
import com.daily.dayo.domain.model.NetworkResponse

interface SearchRepository {

    suspend fun requestSearchTag(tag: String): NetworkResponse<SearchResultResponse>
    suspend fun requestSearchKeyword(keyword: String): NetworkResponse<SearchResultResponse>
    fun requestSearchKeywordRecentList(): ArrayList<String>
    fun clearSearchKeywordRecent()
    fun deleteSearchKeywordRecent(keyword: String)
}