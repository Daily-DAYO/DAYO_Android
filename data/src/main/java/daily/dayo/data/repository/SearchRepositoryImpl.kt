package daily.dayo.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import daily.dayo.data.datasource.local.SharedManager
import daily.dayo.data.datasource.remote.search.SearchApiService
import daily.dayo.data.datasource.remote.search.SearchUserPagingSource
import daily.dayo.data.datasource.remote.search.SearchPagingSource
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Search
import daily.dayo.domain.model.SearchHistory
import daily.dayo.domain.model.SearchHistoryDetail
import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.domain.model.SearchUser
import daily.dayo.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchApiService: SearchApiService,
    private val context: Context
) : SearchRepository {

    override fun requestSearchKeywordRecentList(): SearchHistory =
        SharedManager(context = context).getSearchKeywordRecent()

    override fun deleteSearchKeywordRecent(keyword: String, deleteKeywordType: SearchHistoryType): SearchHistory {
        val deletedSearchHistory = SharedManager(context = context).getSearchKeywordRecent().copy(
            count = SharedManager(context = context).getSearchKeywordRecent().count - 1,
            data = (SharedManager(context = context).getSearchKeywordRecent().data).filter {
                !(it.history == keyword && it.searchHistoryType == deleteKeywordType)
            }
        )

        SharedManager(context = context).saveSearchHistory(deletedSearchHistory)
        return requestSearchKeywordRecentList()
    }

    override fun clearSearchKeywordRecent(): SearchHistory {
        SharedManager(context = context).clearSearchHistory()
        return requestSearchKeywordRecentList()
    }

    override fun updateSearchKeywordRecentList(keyword: String, requestSearchType: SearchHistoryType) {
        SharedManager(context = context).updateSearchHistory(
            SearchHistoryDetail(
            history = keyword,
            searchHistoryType = requestSearchType,
            searchId = 0
        ))
    }

    override fun requestSearchUser(nickname: String): Flow<PagingData<SearchUser>> = Pager(PagingConfig(pageSize = SEARCH_PAGE_SIZE)) {
        SearchUserPagingSource(searchApiService, SEARCH_PAGE_SIZE, nickname)
    }.flow

    override fun requestSearchTag(tag: String): Flow<PagingData<Search>> = Pager(PagingConfig(pageSize = SEARCH_PAGE_SIZE)) {
        SearchPagingSource(searchApiService, SEARCH_PAGE_SIZE, tag)
    }.flow

    override suspend fun requestSearchTotalCount(tag: String, end: Int, searchHistoryType: SearchHistoryType) : Int =
        when (searchHistoryType) {
            SearchHistoryType.TAG -> {
                searchApiService.requestSearchTag(tag, end).let { ApiResponse ->
                    when(ApiResponse) {
                        is NetworkResponse.Success -> {
                            return ApiResponse.body!!.totalCount
                        }
                        else -> return 0
                    }
                }
            }
            SearchHistoryType.USER -> {
                searchApiService.requestSearchUser(tag, end).let { ApiResponse ->
                    when(ApiResponse) {
                        is NetworkResponse.Success -> {
                            return ApiResponse.body!!.totalCount
                        }
                        else -> return 0
                    }
                }
            }
            else -> 0
        }

    companion object {
        private const val SEARCH_PAGE_SIZE = 10
    }
}