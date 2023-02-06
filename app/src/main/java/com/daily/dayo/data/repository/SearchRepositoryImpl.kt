package com.daily.dayo.data.repository

import com.daily.dayo.DayoApplication
import com.daily.dayo.data.datasource.remote.search.SearchApiService
import com.daily.dayo.data.datasource.remote.search.SearchResultResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchApiService: SearchApiService
) : SearchRepository {

    override fun requestSearchKeywordRecentList(): ArrayList<String> =
        DayoApplication.preferences.getSearchKeywordRecent()

    override suspend fun requestSearchTag(tag: String): NetworkResponse<SearchResultResponse> =
        searchApiService.requestSearchTag(tag)

    override suspend fun requestSearchKeyword(keyword: String): NetworkResponse<SearchResultResponse> {
        val initialSearchTagList = requestSearchKeywordRecentList()
        if (initialSearchTagList.contains(keyword)) { // 검색한 적 있는 경우 최신화를 위하여 삭제하고 추가
            initialSearchTagList.remove(keyword)
        }
        initialSearchTagList.add(keyword)
        DayoApplication.preferences.setSearchKeywordRecent(initialSearchTagList)
        return requestSearchTag(tag = keyword)
    }

    override fun deleteSearchKeywordRecent(keyword: String) {
        val initialSearchTagList = requestSearchKeywordRecentList()
        initialSearchTagList.remove(keyword)
        DayoApplication.preferences.setSearchKeywordRecent(initialSearchTagList)
    }

    override fun clearSearchKeywordRecent() {
        DayoApplication.preferences.setSearchKeywordRecent(ArrayList<String>())
    }
}