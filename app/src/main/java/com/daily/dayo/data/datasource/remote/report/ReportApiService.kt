package com.daily.dayo.data.datasource.remote.report

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApiService {

    @POST("/api/v1/reports/member")
    suspend fun requestSaveMemberReport(@Body body: CreateReportMemberRequest): Response<Void>

    @POST("/api/v1/reports/post")
    suspend fun requestSavePostReport(@Body body: CreateReportPostRequest): Response<Void>
}