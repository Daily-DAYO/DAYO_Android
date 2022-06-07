package com.daily.dayo.data.repository

import com.daily.dayo.DayoApplication
import com.daily.dayo.data.datasource.remote.search.SearchApiService
import com.daily.dayo.data.datasource.remote.search.SearchResultResponse
import com.daily.dayo.domain.repository.SearchRepository
import retrofit2.Response
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchApiService: SearchApiService
) : SearchRepository {

    override fun requestSearchKeywordRecentList(): ArrayList<String> =
        DayoApplication.preferences.getSearchKeywordRecent()

    override suspend fun requestSearchTag(tag: String): Response<SearchResultResponse> =
        searchApiService.requestSearchTag(tag)

    override suspend fun requestSearchKeyword(keyword: String): Response<SearchResultResponse> {
        if (requestSearchKeywordRecentList().contains(keyword)) { // 검색한 적 있는 경우 최신화를 위하여 삭제하고 추가
            requestSearchKeywordRecentList().remove(keyword)
        }
        requestSearchKeywordRecentList().add(keyword)
        DayoApplication.preferences.setSearchKeywordRecent(requestSearchKeywordRecentList())

        return requestSearchTag(tag = keyword)
    }

    override fun deleteSearchKeywordRecent(keyword: String) {
        requestSearchKeywordRecentList().remove(keyword)
        DayoApplication.preferences.setSearchKeywordRecent(requestSearchKeywordRecentList())
    }

    override fun clearSearchKeywordRecent() {
        requestSearchKeywordRecentList().clear()
        DayoApplication.preferences.setSearchKeywordRecent(requestSearchKeywordRecentList())
    }
}