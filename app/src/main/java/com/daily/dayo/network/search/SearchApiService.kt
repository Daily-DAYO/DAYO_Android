package com.daily.dayo.network.search

import com.daily.dayo.search.model.ResponseSearchTag
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("/api/v1/search")
    suspend fun requestSearchTag(@Query("tag") tag: String): Response<ResponseSearchTag>
}