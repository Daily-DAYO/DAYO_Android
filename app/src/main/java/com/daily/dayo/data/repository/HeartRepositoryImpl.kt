package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.HeartApiService
import com.daily.dayo.data.datasource.remote.heart.HeartPagingSource
import com.daily.dayo.data.datasource.remote.heart.HeartPostUsersPagingSource
import com.daily.dayo.data.mapper.toLikePostDeleteResponse
import com.daily.dayo.data.mapper.toLikePostResponse
import daily.dayo.domain.model.LikePostDeleteResponse
import daily.dayo.domain.model.LikePostResponse
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class HeartRepositoryImpl @Inject constructor(
    private val heartApiService: HeartApiService
) : HeartRepository {

    override suspend fun requestLikePost(postId: Int): NetworkResponse<LikePostResponse> =
        when (val response = heartApiService.requestLikePost(CreateHeartRequest(postId))) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toLikePostResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestUnlikePost(postId: Int): NetworkResponse<LikePostDeleteResponse> =
        when (val response = heartApiService.requestUnlikePost(postId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toLikePostDeleteResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestAllMyLikePostList() = Pager(PagingConfig(pageSize = HEART_PAGE_SIZE)) {
        HeartPagingSource(heartApiService, HEART_PAGE_SIZE)
    }.flow

    override suspend fun requestPostLikeUsers(postId: Int) = Pager(PagingConfig(pageSize = HEART_PAGE_SIZE)) {
        HeartPostUsersPagingSource(heartApiService, HEART_PAGE_SIZE, postId)
    }.flow

    companion object {
        private const val HEART_PAGE_SIZE = 10
    }
}