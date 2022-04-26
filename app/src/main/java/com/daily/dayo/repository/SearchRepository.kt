package com.daily.dayo.repository

import com.daily.dayo.SharedManager
import com.daily.dayo.network.search.SearchApiHelper
import com.daily.dayo.search.model.RequestSearchTag
import com.daily.dayo.search.model.ResponseSearchTag
import retrofit2.Response
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchApiHelper: SearchApiHelper,
    private val sharedManager: SharedManager,
) {
    var searchKeywordRecentList = sharedManager.getSearchKeywordRecent()

    suspend fun searchKeyword(keyword: String): Response<ResponseSearchTag> {
        if (searchKeywordRecentList.contains(keyword)) { // 검색한 적 있는 경우 최신화를 위하여 삭제하고 추가
            searchKeywordRecentList.remove(keyword)
        }
        searchKeywordRecentList.add(keyword)
        sharedManager.setSearchKeywordRecent(searchKeywordRecentList)

        return searchApiHelper.requestSearchTag(RequestSearchTag(keyword)).verify()
    }

    fun deleteSearchKeywordRecent(keyword: String) {
        searchKeywordRecentList.remove(keyword)
        sharedManager.setSearchKeywordRecent(searchKeywordRecentList)
    }

    fun clearSearchKeywordRecent() {
        searchKeywordRecentList.clear()
        sharedManager.setSearchKeywordRecent(searchKeywordRecentList)
    }

    fun Response<ResponseSearchTag>.verify(): Response<ResponseSearchTag> {
        if (this.isSuccessful && this.code() in 200..299) {
            return this
        } else {
            throw Exception("${this.code()}")
        }
    }
}