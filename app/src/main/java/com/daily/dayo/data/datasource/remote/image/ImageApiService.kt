package com.daily.dayo.data.datasource.remote.image

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageApiService {

    @GET("/images/{filename}")
    suspend fun requestDownloadImage(@Path("filename") filename: String): NetworkResponse<Void>
}