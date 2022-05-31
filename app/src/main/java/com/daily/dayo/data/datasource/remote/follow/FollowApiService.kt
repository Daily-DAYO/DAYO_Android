package com.daily.dayo.data.datasource.remote.follow

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FollowApiService {

    @POST("/api/v1/follow")
    suspend fun requestCreateFollow(@Body body: CreateFollowRequest): Response<CreateFollowResponse>

    @POST("/api/v1/follow/delete/{followerId}")
    suspend fun requestDeleteFollow(@Path("followerId") followerId: String): Response<Void>

    @GET("/api/v1/follow/follower/list/{memberId}")
    suspend fun requestListAllFollower(@Path("memberId") memberId: String): Response<ListAllFollowerResponse>

    @GET("/api/v1/follow/follower/my")
    suspend fun requestListAllMyFollower(): Response<ListAllMyFollowerResponse>

    @GET("/api/v1/follow/following/list/{memberId}")
    suspend fun requestListAllFollowing(@Path("memberId") memberId: String): Response<ListAllFollowingResponse>

    @GET("/api/v1/follow/following/my")
    suspend fun requestListAllMyFollowing(): Response<ListAllMyFollowingResponse>

    @POST("/api/v1/follow/up")
    suspend fun requestCreateFollowUp(@Body body: CreateFollowUpRequest): Response<CreateFollowUpResponse>
}