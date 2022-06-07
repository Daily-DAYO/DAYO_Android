package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.data.datasource.remote.heart.ListAllMyHeartPostResponse
import retrofit2.Response

interface HeartRepository {

    suspend fun requestLikePost(body: CreateHeartRequest): Response<CreateHeartResponse>
    suspend fun requestUnlikePost(postId: Int): Response<Void>
    suspend fun requestAllMyLikePostList(): Response<ListAllMyHeartPostResponse>
}