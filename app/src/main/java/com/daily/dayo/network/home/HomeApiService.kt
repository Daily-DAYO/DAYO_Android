package com.daily.dayo.network.home

import com.daily.dayo.home.model.ResponseHomePost
import retrofit2.http.GET

interface HomeApiService {
    @GET("/api/v1/posts")
    suspend fun requestPostList(): ResponseHomePost
}