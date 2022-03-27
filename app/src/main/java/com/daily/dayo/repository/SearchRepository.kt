package com.daily.dayo.repository

import com.daily.dayo.SharedManager
import javax.inject.Inject

class SearchRepository @Inject constructor(private val sharedManager: SharedManager) {
    var searchKeywordRecentList = sharedManager.getSearchKeywordRecent()

    fun searchKeyword(keyword: String) {
        if(searchKeywordRecentList.contains(keyword)) { // 검색한 적 있는 경우 최신화를 위하여 삭제하고 추가
            searchKeywordRecentList.remove(keyword)
        }
        searchKeywordRecentList.add(keyword)
        sharedManager.setSearchKeywordRecent(searchKeywordRecentList)
    }

    fun deleteSearchKeywordRecent(keyword: String) {
        searchKeywordRecentList.remove(keyword)
        sharedManager.setSearchKeywordRecent(searchKeywordRecentList)
    }

    fun clearSearchKeywordRecent() {
        searchKeywordRecentList.clear()
        sharedManager.setSearchKeywordRecent(searchKeywordRecentList)
    }
}