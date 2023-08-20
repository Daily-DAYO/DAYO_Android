package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.report.CreateReportMemberRequest
import com.daily.dayo.data.datasource.remote.report.CreateReportPostRequest
import com.daily.dayo.data.datasource.remote.report.ReportApiService
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportApiService: ReportApiService
) : ReportRepository {
    override suspend fun requestSaveMemberReport(
        comment: String,
        memberId: String
    ): NetworkResponse<Void> =
        reportApiService.requestSaveMemberReport(
            CreateReportMemberRequest(
                comment = comment,
                memberId = memberId
            )
        )

    override suspend fun requestSavePostReport(
        comment: String,
        postId: Int
    ): NetworkResponse<Void> =
        reportApiService.requestSavePostReport(
            CreateReportPostRequest(
                comment = comment,
                postId = postId
            )
        )
}