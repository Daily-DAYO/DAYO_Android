package daily.dayo.data.datasource.remote.alarm

import androidx.paging.PagingSource
import androidx.paging.PagingState
import daily.dayo.data.mapper.toNotification
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Notification

class AlarmPagingSource(
    private val apiService: AlarmApiService,
    private val size: Int
) : PagingSource<Int, Notification>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Notification> {
        val nextPageNumber = params.key ?: 0
        apiService.requestAllAlarmList(end = nextPageNumber).let { ApiResponse ->
            return try {
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        return LoadResult.Page(
                            data = ApiResponse.body!!.data.map { it.toNotification() },
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

    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(size) ?: anchorPage?.nextKey?.minus(size)
        }
    }
}