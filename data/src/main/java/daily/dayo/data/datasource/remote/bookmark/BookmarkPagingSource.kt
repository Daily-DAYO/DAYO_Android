package daily.dayo.data.datasource.remote.bookmark

import androidx.paging.PagingSource
import androidx.paging.PagingState
import daily.dayo.data.mapper.toBookmarkPost
import daily.dayo.domain.model.BookmarkPost
import daily.dayo.domain.model.NetworkResponse

class BookmarkPagingSource(
    private val apiService: BookmarkApiService,
    private val size: Int
) : PagingSource<Int, BookmarkPost>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, BookmarkPost> {
        val nextPageNumber = params.key ?: 0
        apiService.requestAllMyBookmarkPostList(end = nextPageNumber).let { ApiResponse ->
            return try {
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = ApiResponse.body!!.data.map { it.toBookmarkPost() },
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

    override fun getRefreshKey(state: PagingState<Int, BookmarkPost>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}