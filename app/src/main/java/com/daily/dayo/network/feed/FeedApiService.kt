package com.daily.dayo.network.feed

import com.daily.dayo.feed.model.ResponseFeedList
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.post.model.ResponseLikePost
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FeedApiService {
    @GET("/api/v1/posts/feed/list")
    suspend fun requestFeedList(): Response<ResponseFeedList>
    @POST("/api/v1/heart")
    suspend fun requestLikePost(@Body body : RequestLikePost) : Response<ResponseLikePost>
    @POST("/api/v1/heart/delete/{postId}")
    suspend fun requestUnlikePost(@Path("postId") postId : Int) : Response<Void>
}