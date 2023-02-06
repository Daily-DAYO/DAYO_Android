package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.data.datasource.remote.heart.ListAllMyHeartPostResponse
import com.daily.dayo.domain.model.NetworkResponse

interface HeartRepository {

    suspend fun requestLikePost(body: CreateHeartRequest): NetworkResponse<CreateHeartResponse>
    suspend fun requestUnlikePost(postId: Int): NetworkResponse<Void>
    suspend fun requestAllMyLikePostList(): NetworkResponse<ListAllMyHeartPostResponse>
}