package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.search.SearchResultResponse
import retrofit2.Response

interface SearchRepository {

    suspend fun requestSearchTag(tag: String): Response<SearchResultResponse>
    suspend fun requestSearchKeyword(keyword: String): Response<SearchResultResponse>
    fun requestSearchKeywordRecentList(): ArrayList<String>
    fun clearSearchKeywordRecent()
    fun deleteSearchKeywordRecent(keyword: String)
}