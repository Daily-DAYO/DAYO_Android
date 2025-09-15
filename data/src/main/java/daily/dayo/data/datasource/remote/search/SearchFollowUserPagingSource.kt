package daily.dayo.data.datasource.remote.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import daily.dayo.data.mapper.toSearchUser
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.SearchUser

class SearchFollowUserPagingSource(
    private val apiService: SearchApiService,
    private val size: Int,
    private val nickname: String
) : PagingSource<Int, SearchUser>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, SearchUser> {
        val nextPageNumber = params.key ?: 0
        apiService.requestSearchFollowUser(nickname = nickname, end = nextPageNumber)
            .let { ApiResponse ->
                return try {
                    when (ApiResponse) {
                        is NetworkResponse.Success -> {
                            return LoadResult.Page(
                                data = ApiResponse.body!!.data.map { it.toSearchUser() },
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

    override fun getRefreshKey(state: PagingState<Int, SearchUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}
