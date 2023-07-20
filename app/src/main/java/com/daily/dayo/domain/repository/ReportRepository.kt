package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.report.CreateReportMemberRequest
import com.daily.dayo.data.datasource.remote.report.CreateReportPostRequest
import com.daily.dayo.domain.model.NetworkResponse

interface ReportRepository {

    suspend fun requestSaveMemberReport(body: CreateReportMemberRequest): NetworkResponse<Void>
    suspend fun requestSavePostReport(body: CreateReportPostRequest): NetworkResponse<Void>
}