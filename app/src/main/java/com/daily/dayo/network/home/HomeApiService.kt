package com.daily.dayo.network.home

import com.daily.dayo.home.model.RequestLikePost
import com.daily.dayo.home.model.ResponseHomePost
import com.daily.dayo.home.model.ResponseLikePost
import retrofit2.Response
import retrofit2.http.*

interface HomeApiService {
    // (HomeApiService will service our network call)
    // 네트워크 작업 Call
    @GET("/api/v1/posts")
    suspend fun requestPostList(): Response<ResponseHomePost>
    @GET("/api/v1/posts/category/{category}")
    suspend fun requestPostListCategory(@Path("category") category : String): Response<ResponseHomePost>
    @POST("/api/v1/heart")
    suspend fun requestLikePost(@Body body : RequestLikePost) : Response<ResponseLikePost>
    @POST("/api/v1/heart/delete/{postId}")
    suspend fun requestUnlikePost(@Path("postId") postId : Int) : Response<Void>
}