package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.daily.dayo.DayoApplication
import com.daily.dayo.data.datasource.remote.search.SearchApiService
import com.daily.dayo.data.datasource.remote.search.SearchPagingSource
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Search
import daily.dayo.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchApiService: SearchApiService
) : SearchRepository {

    override fun requestSearchKeywordRecentList(): ArrayList<String> =
        DayoApplication.preferences.getSearchKeywordRecent()

    override fun deleteSearchKeywordRecent(keyword: String) {
        val initialSearchTagList = requestSearchKeywordRecentList()
        initialSearchTagList.remove(keyword)
        DayoApplication.preferences.setSearchKeywordRecent(initialSearchTagList)
    }

    override fun clearSearchKeywordRecent() {
        DayoApplication.preferences.setSearchKeywordRecent(ArrayList())
    }

    override fun requestSearchKeyword(keyword: String): Flow<PagingData<Search>> {
        val initialSearchTagList = requestSearchKeywordRecentList()
        if (initialSearchTagList.contains(keyword)) { // 검색한 적 있는 경우 최신화를 위하여 삭제하고 추가
            initialSearchTagList.remove(keyword)
        }
        initialSearchTagList.add(keyword)
        DayoApplication.preferences.setSearchKeywordRecent(initialSearchTagList)
        return requestSearchTag(tag = keyword)
    }

    override fun requestSearchTag(tag: String) = Pager(PagingConfig(pageSize = SEARCH_PAGE_SIZE)) {
        SearchPagingSource(searchApiService, SEARCH_PAGE_SIZE, tag)
    }.flow

    override suspend fun requestSearchTotalCount(tag: String, end: Int) : Int =
        searchApiService.requestSearchTag(tag, end).let { ApiResponse ->
            when(ApiResponse) {
                is NetworkResponse.Success -> {
                    return ApiResponse.body!!.totalCount
                }
                else -> return 0
            }
        }

    companion object {
        private const val SEARCH_PAGE_SIZE = 10
    }
}