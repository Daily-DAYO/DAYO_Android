package daily.dayo.data.datasource.remote.heart

import androidx.paging.PagingSource
import androidx.paging.PagingState
import daily.dayo.data.mapper.toLikeUser
import daily.dayo.domain.model.LikeUser
import daily.dayo.domain.model.NetworkResponse

class HeartPostUsersPagingSource(
    private val apiService: HeartApiService,
    private val size: Int,
    private val postId: Int
) : PagingSource<Int, LikeUser>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, LikeUser> {
        val nextPageNumber = params.key ?: 0

        apiService.requestPostLikeUsers(postId = postId, end = nextPageNumber).let { ApiResponse ->
            return try {
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = ApiResponse.body!!.data.map { it.toLikeUser() },
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

    override fun getRefreshKey(state: PagingState<Int, LikeUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}
