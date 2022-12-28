package com.daily.dayo.data.datasource.remote.block

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BlockApiService {

    @POST("/api/v1/block")
    suspend fun requestBlockMember(@Body body: BlockRequest): Response<Void>
}