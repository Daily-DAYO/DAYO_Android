package com.daily.dayo.domain.repository

import androidx.paging.PagingData
import com.daily.dayo.domain.model.Search
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun requestSearchKeyword(keyword: String): Flow<PagingData<Search>>
    fun requestSearchTag(tag: String): Flow<PagingData<Search>>
    fun requestSearchKeywordRecentList(): ArrayList<String>
    fun clearSearchKeywordRecent()
    fun deleteSearchKeywordRecent(keyword: String)
    suspend fun requestSearchTotalCount(tag: String, end: Int): Int
}