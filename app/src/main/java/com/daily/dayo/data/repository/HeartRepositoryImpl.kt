package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.data.datasource.remote.heart.HeartApiService
import com.daily.dayo.data.datasource.remote.heart.ListAllMyHeartPostResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class HeartRepositoryImpl @Inject constructor(
    private val heartApiService: HeartApiService
) : HeartRepository {

    override suspend fun requestLikePost(body: CreateHeartRequest): NetworkResponse<CreateHeartResponse> =
        heartApiService.requestLikePost(body)

    override suspend fun requestUnlikePost(postId: Int): NetworkResponse<Void> =
        heartApiService.requestUnlikePost(postId)

    override suspend fun requestAllMyLikePostList(): NetworkResponse<ListAllMyHeartPostResponse> =
        heartApiService.requestAllMyLikePostList()
}