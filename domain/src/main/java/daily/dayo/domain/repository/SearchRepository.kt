package daily.dayo.domain.repository

import androidx.paging.PagingData
import daily.dayo.domain.model.Search
import daily.dayo.domain.model.SearchHistory
import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.domain.model.SearchUser
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun requestSearchTag(tag: String): Flow<PagingData<Search>>
    fun requestSearchUser(nickname: String): Flow<PagingData<SearchUser>>
    fun requestSearchFollowUser(nickname: String): Flow<PagingData<SearchUser>>
    fun requestSearchKeywordRecentList(): SearchHistory
    fun updateSearchKeywordRecentList(keyword: String, requestSearchType: SearchHistoryType)
    fun clearSearchKeywordRecent(): SearchHistory
    fun deleteSearchKeywordRecent(
        keyword: String,
        deleteKeywordType: SearchHistoryType
    ): SearchHistory

    suspend fun requestSearchTotalCount(
        tag: String,
        end: Int,
        searchHistoryType: SearchHistoryType
    ): Int
}