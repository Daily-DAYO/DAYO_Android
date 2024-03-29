package daily.dayo.data.datasource.remote.heart

import androidx.paging.PagingSource
import androidx.paging.PagingState
import daily.dayo.data.mapper.toLikePost
import daily.dayo.domain.model.LikePost
import daily.dayo.domain.model.NetworkResponse

class HeartPagingSource(
    private val apiService: HeartApiService,
    private val size: Int
) : PagingSource<Int, LikePost>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, LikePost> {
        val nextPageNumber = params.key ?: 0

        apiService.requestAllMyLikePostList(end = nextPageNumber).let { ApiResponse ->
            return try {
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = ApiResponse.body!!.data.map { it.toLikePost() },
                            prevKey = if(nextPageNumber == 0) null else nextPageNumber - size,
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

    override fun getRefreshKey(state: PagingState<Int, LikePost>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}
