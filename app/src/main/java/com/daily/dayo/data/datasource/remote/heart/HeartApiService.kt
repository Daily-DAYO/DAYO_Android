package com.daily.dayo.data.datasource.remote.heart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HeartApiService {

    @POST("/api/v1/heart")
    suspend fun requestLikePost(@Body body: CreateHeartRequest): Response<CreateHeartResponse>

    @POST("/api/v1/heart/delete/{postId}")
    suspend fun requestUnlikePost(@Path("postId") postId: Int): Response<Void>

    @GET("/api/v1/heart/list")
    suspend fun requestAllMyLikePostList(): Response<ListAllMyHeartPostResponse>
}