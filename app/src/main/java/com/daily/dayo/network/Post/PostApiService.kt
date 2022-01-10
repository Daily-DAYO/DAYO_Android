package com.daily.dayo.network.Post

import com.daily.dayo.post.model.RequestCreatePostComment
import com.daily.dayo.post.model.ResponseCreatePostComment
import com.daily.dayo.post.model.ResponsePost
import com.daily.dayo.post.model.ResponsePostComment
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApiService {
    @GET("/api/v1/posts/{postId}")
    suspend fun requestPostDetail(@Path("postId") postId : Int) : Response<ResponsePost>
    @GET("/api/v1/comments/{postId}")
    suspend fun requestPostComment(@Path("postId") postId : Int) : Response<ResponsePostComment>
    @POST("/api/v1/comments")
    suspend fun requestCreatePostComment(@Body body : RequestCreatePostComment) : Response<ResponseCreatePostComment>
}