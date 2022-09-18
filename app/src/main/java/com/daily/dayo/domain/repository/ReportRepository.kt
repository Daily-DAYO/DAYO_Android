package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.report.CreateReportMemberRequest
import com.daily.dayo.data.datasource.remote.report.CreateReportPostRequest
import retrofit2.Response

interface ReportRepository {

    suspend fun requestSaveMemberReport(body: CreateReportMemberRequest): Response<Void>
    suspend fun requestSavePostReport(body: CreateReportPostRequest): Response<Void>
}