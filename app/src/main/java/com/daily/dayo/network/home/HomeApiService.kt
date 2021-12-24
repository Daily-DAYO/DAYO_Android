package com.daily.dayo.network.home

import com.daily.dayo.home.model.ResponseHomePost
import retrofit2.Response
import retrofit2.http.GET

interface HomeApiService {
    // (HomeApiService will service our network call)
    // 네트워크 작업 Call
    @GET("/api/v1/posts")
    suspend fun requestPostList(): Response<ResponseHomePost>
}