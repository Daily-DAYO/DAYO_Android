package com.daily.dayo.network.Post

import com.daily.dayo.post.model.ResponsePost
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {
    @GET("/api/v1/posts/{postId}")
    suspend fun requestPostDetail(@Path("postId") postId : Int) : Response<ResponsePost>
}