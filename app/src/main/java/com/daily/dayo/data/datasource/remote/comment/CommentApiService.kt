package com.daily.dayo.data.datasource.remote.comment

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApiService {

    @POST("/api/v1/comments")
    suspend fun requestCreatePostComment(@Body body: CreateCommentRequest): Response<CreateCommentResponse>

    @GET("/api/v1/comments/{postId}")
    suspend fun requestPostComment(@Path("postId") postId: Int): Response<ListAllCommentResponse>

    @POST("/api/v1/comments/delete/{commentId}")
    suspend fun requestDeletePostComment(@Path("commentId") commentId: Int): Response<Void>
}