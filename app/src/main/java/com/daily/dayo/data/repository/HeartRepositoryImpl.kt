package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.data.datasource.remote.heart.DeleteHeartResponse
import com.daily.dayo.data.datasource.remote.heart.HeartApiService
import com.daily.dayo.data.datasource.remote.heart.HeartPagingSource
import com.daily.dayo.data.datasource.remote.heart.HeartPostUsersPagingSource
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class HeartRepositoryImpl @Inject constructor(
    private val heartApiService: HeartApiService
) : HeartRepository {

    override suspend fun requestLikePost(body: CreateHeartRequest): NetworkResponse<CreateHeartResponse> =
        heartApiService.requestLikePost(body)

    override suspend fun requestUnlikePost(postId: Int): NetworkResponse<DeleteHeartResponse> =
        heartApiService.requestUnlikePost(postId)

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