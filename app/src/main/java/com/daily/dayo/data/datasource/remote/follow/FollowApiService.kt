package com.daily.dayo.data.datasource.remote.follow

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FollowApiService {

    @POST("/api/v1/follow")
    suspend fun requestCreateFollow(@Body body: CreateFollowRequest): NetworkResponse<CreateFollowResponse>

    @POST("/api/v1/follow/delete/{followerId}")
    suspend fun requestDeleteFollow(@Path("followerId") followerId: String): NetworkResponse<Void>

    @GET("/api/v1/follow/follower/list/{memberId}")
    suspend fun requestListAllFollower(@Path("memberId") memberId: String): NetworkResponse<ListAllFollowerResponse>

    @GET("/api/v1/follow/following/list/{memberId}")
    suspend fun requestListAllFollowing(@Path("memberId") memberId: String): NetworkResponse<ListAllFollowingResponse>

    @POST("/api/v1/follow/up")
    suspend fun requestCreateFollowUp(@Body body: CreateFollowUpRequest): NetworkResponse<CreateFollowUpResponse>
}