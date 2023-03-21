package com.daily.dayo.data.datasource.remote.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.daily.dayo.data.mapper.toSearch
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Search

class SearchPagingSource(
    private val apiService: SearchApiService,
    private val size: Int,
    private val tag: String
) : PagingSource<Int, Search>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Search> {
        val nextPageNumber = params.key ?: 0
        apiService.requestSearchTag(tag = tag, end = nextPageNumber).let { ApiResponse ->
            return try {
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = ApiResponse.body!!.data.map { it.toSearch() },
                            prevKey = if (nextPageNumber == 0) null else nextPageNumber - size,
                            nextKey = if (ApiResponse.body.last || ApiResponse.body.count == 0) null else nextPageNumber + size
                        )
                    }
                    else -> {
                        throw Exception("LoadResult Error")
                    }
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Search>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}
