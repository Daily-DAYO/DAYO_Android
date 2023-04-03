package com.daily.dayo.data.datasource.remote.search

import com.daily.dayo.domain.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {

    @GET("/api/v1/search")
    suspend fun requestSearchTag(@Query("tag") tag: String, @Query("end") end: Int): NetworkResponse<SearchResultResponse>
}