package com.daily.dayo.domain.usecase.report

import com.daily.dayo.data.datasource.remote.report.CreateReportMemberRequest
import com.daily.dayo.domain.repository.ReportRepository
import retrofit2.Response
import javax.inject.Inject

class RequestSaveMemberReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(body: CreateReportMemberRequest): Response<Void> =
        reportRepository.requestSaveMemberReport(body)
}