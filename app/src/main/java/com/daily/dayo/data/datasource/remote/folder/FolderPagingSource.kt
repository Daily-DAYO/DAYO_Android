package com.daily.dayo.data.datasource.remote.folder

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.daily.dayo.data.mapper.toFolderPost
import daily.dayo.domain.model.FolderPost
import daily.dayo.domain.model.NetworkResponse

class FolderPagingSource(
    private val apiService: FolderApiService,
    private val size: Int,
    private val folderId: Int
) : PagingSource<Int, FolderPost>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, FolderPost> {
        val nextPageNumber = params.key ?: 0
        apiService.requestDetailListFolder(folderId = folderId, end = nextPageNumber).let { ApiResponse ->
            return try {
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = ApiResponse.body!!.posts.map { it.toFolderPost() },
                            prevKey = if (nextPageNumber == 0) null else nextPageNumber - size,
                            nextKey = if (ApiResponse.body!!.last || ApiResponse.body!!.count == 0) null else nextPageNumber + size
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

    override fun getRefreshKey(state: PagingState<Int, FolderPost>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}
