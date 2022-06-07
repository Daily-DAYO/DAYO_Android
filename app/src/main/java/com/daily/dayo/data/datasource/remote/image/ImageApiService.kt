package com.daily.dayo.data.datasource.remote.image

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageApiService {

    @GET("/images/{filename}")
    suspend fun requestDownloadImage(@Path("filename") filename: String): Response<Void>
}