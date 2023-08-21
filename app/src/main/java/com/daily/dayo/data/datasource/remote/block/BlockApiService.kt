package com.daily.dayo.data.datasource.remote.block

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BlockApiService {

    @POST("/api/v1/block")
    suspend fun requestBlockMember(@Body body: BlockRequest): NetworkResponse<Void>

    @POST("/api/v1/block/cancel")
    suspend fun requestUnblockMember(@Body body: UnblockRequest): NetworkResponse<Void>
}