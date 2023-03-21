package com.daily.dayo.domain.repository

import androidx.paging.PagingData
import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.domain.model.LikePost
import com.daily.dayo.domain.model.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface HeartRepository {

    suspend fun requestLikePost(body: CreateHeartRequest): NetworkResponse<CreateHeartResponse>
    suspend fun requestUnlikePost(postId: Int): NetworkResponse<Void>
    fun requestAllMyLikePostList(): Flow<PagingData<LikePost>>
}