package com.daily.dayo.data.datasource.remote.notice

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.daily.dayo.data.mapper.toNotice
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Notice

class NoticePagingSource(
    private val apiService: NoticeApiService,
    private val size: Int
) : PagingSource<Int, Notice>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Notice> {
        val nextPageNumber = params.key ?: 0
        apiService.requestAllNoticeList(end = nextPageNumber).let { ApiResponse ->
            return try {
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = ApiResponse.body!!.data.map { it.toNotice() },
                            prevKey = null,
                            nextKey = if (ApiResponse.body.last) null else nextPageNumber + size
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

    override fun getRefreshKey(state: PagingState<Int, Notice>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}