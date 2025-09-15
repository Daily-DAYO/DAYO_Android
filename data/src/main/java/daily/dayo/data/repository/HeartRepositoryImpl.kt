package daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import daily.dayo.data.datasource.remote.heart.HeartApiService
import daily.dayo.data.datasource.remote.heart.HeartPagingSource
import daily.dayo.data.datasource.remote.heart.HeartPostUsersPagingSource
import daily.dayo.data.mapper.toLikePostDeleteResponse
import daily.dayo.data.mapper.toLikePostResponse
import daily.dayo.domain.model.LikePostResponse
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class HeartRepositoryImpl @Inject constructor(
    private val heartApiService: HeartApiService
) : HeartRepository {

    override suspend fun requestLikePost(postId: Long): NetworkResponse<LikePostResponse> =
        when (val response = heartApiService.requestLikePost(CreateHeartRequest(postId))) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toLikePostResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestUnlikePost(postId: Long): NetworkResponse<LikePostResponse> =
        when (val response = heartApiService.requestUnlikePost(postId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toLikePostDeleteResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestAllMyLikePostList() = Pager(PagingConfig(pageSize = HEART_PAGE_SIZE)) {
        HeartPagingSource(heartApiService, HEART_PAGE_SIZE)
    }.flow

    override suspend fun requestPostLikeUsers(postId: Long) = Pager(PagingConfig(pageSize = HEART_PAGE_SIZE)) {
        HeartPostUsersPagingSource(heartApiService, HEART_PAGE_SIZE, postId)
    }.flow

    companion object {
        private const val HEART_PAGE_SIZE = 10
    }
}