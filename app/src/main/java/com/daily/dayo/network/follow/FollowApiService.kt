package com.daily.dayo.network.follow

import com.daily.dayo.profile.model.RequestCreateFollow
import com.daily.dayo.profile.model.ResponseCreateFollow
import com.daily.dayo.profile.model.ResponseListAllFollow
import retrofit2.Response
import retrofit2.http.*

interface FollowApiService {

    @POST("/api/v1/follow")
    suspend fun requestCreateFollow(@Body body: RequestCreateFollow): Response<ResponseCreateFollow>

    @POST("/api/v1/follow/delete/{followerId}")
    suspend fun requestDeleteFollow(@Path("followerId") followerId: String): Response<Void>

    @GET("/api/v1/follow/follower/list/{memberId}")
    suspend fun requestListAllFollower(@Path("memberId") memberId: String): Response<ResponseListAllFollow>

    @GET("/api/v1/follow/follower/my")
    suspend fun requestListAllMyFollower(): Response<ResponseListAllFollow>

    @GET("/api/v1/follow/following/list/{memberId}")
    suspend fun requestListAllFollowing(@Path("memberId") memberId: String) : Response<ResponseListAllFollow>

    @GET("/api/v1/follow/following/my")
    suspend fun requestListAllMyFollowing(): Response<ResponseListAllFollow>

    @POST("/api/v1/follow/up")
    suspend fun requestCreateFollowUp(@Body body:RequestCreateFollow):Response<ResponseCreateFollow>

}