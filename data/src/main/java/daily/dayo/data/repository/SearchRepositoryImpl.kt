package daily.dayo.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import daily.dayo.data.datasource.local.SharedManager
import daily.dayo.data.datasource.remote.search.SearchApiService
import daily.dayo.data.datasource.remote.search.SearchPagingSource
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Search
import daily.dayo.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchApiService: SearchApiService,
    private val context: Context
) : SearchRepository {

    override fun requestSearchKeywordRecentList(): ArrayList<String> =
        SharedManager(context = context).getSearchKeywordRecent()

    override fun deleteSearchKeywordRecent(keyword: String) {
        val initialSearchTagList = requestSearchKeywordRecentList()
        initialSearchTagList.remove(keyword)
        SharedManager(context = context).setSearchKeywordRecent(initialSearchTagList)
    }

    override fun clearSearchKeywordRecent() {
        SharedManager(context = context).setSearchKeywordRecent(ArrayList())
    }

    override fun requestSearchKeyword(keyword: String): Flow<PagingData<Search>> {
        val initialSearchTagList = requestSearchKeywordRecentList()
        if (initialSearchTagList.contains(keyword)) { // 검색한 적 있는 경우 최신화를 위하여 삭제하고 추가
            initialSearchTagList.remove(keyword)
        }
        initialSearchTagList.add(keyword)
        SharedManager(context = context).setSearchKeywordRecent(initialSearchTagList)
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