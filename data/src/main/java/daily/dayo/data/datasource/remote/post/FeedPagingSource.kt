package daily.dayo.data.datasource.remote.post

import androidx.paging.PagingSource
import androidx.paging.PagingState
import daily.dayo.data.mapper.toPost
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Post

class FeedPagingSource(
    private val category: Category,
    private val apiService: PostApiService,
    private val size: Int
) : PagingSource<Int, Post>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Post> {
        val nextPageNumber = params.key ?: 0
        if (category == Category.ALL) {
            apiService.requestAllFeedList(end = nextPageNumber)
        } else {
            apiService.requestFeedListByCategory(category = category, end = nextPageNumber)
        }.let { response ->
            return try {
                when (response) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = response.body?.data?.map { it.toPost() } ?: emptyList(),
                            prevKey = if (nextPageNumber == 0) null else nextPageNumber - size,
                            nextKey = if (response.body?.last != false || response.body?.count == 0) null else nextPageNumber + size
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

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}
