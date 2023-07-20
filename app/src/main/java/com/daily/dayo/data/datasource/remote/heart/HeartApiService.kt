package com.daily.dayo.data.datasource.remote.heart

import com.daily.dayo.domain.model.NetworkResponse
import retrofit2.http.*

interface HeartApiService {

    @POST("/api/v1/heart")
    suspend fun requestLikePost(@Body body: CreateHeartRequest): NetworkResponse<CreateHeartResponse>

    @POST("/api/v1/heart/delete/{postId}")
    suspend fun requestUnlikePost(@Path("postId") postId: Int): NetworkResponse<DeleteHeartResponse>

    @GET("/api/v1/heart/list")
    suspend fun requestAllMyLikePostList(@Query("end") end: Int): NetworkResponse<ListAllMyHeartPostResponse>
}