package com.daily.dayo.network.post

import com.daily.dayo.post.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApiService {
    @GET("/api/v1/posts/{postId}")
    suspend fun requestPostDetail(@Path("postId") postId : Int) : Response<ResponsePost>
    @POST("/api/v1/posts/delete/{postId}")
    suspend fun requestDeletePost(@Path("postId") postId : Int) : Response<Void>
    @POST("/api/v1/heart")
    suspend fun requestLikePost(@Body body : RequestLikePost) : Response<ResponseLikePost>
    @POST("/api/v1/heart/delete/{postId}")
    suspend fun requestUnlikePost(@Path("postId") postId : Int) : Response<Void>
    @GET("/api/v1/comments/{postId}")
    suspend fun requestPostComment(@Path("postId") postId : Int) : Response<ResponsePostComment>
    @POST("/api/v1/comments")
    suspend fun requestCreatePostComment(@Body body : RequestCreatePostComment) : Response<ResponseCreatePostComment>
    @POST("/api/v1/comments/delete/{commentId}")
    suspend fun requestDeletePostComment(@Path("commentId") commentId : Int) : Response<Void>
}