package com.daily.dayo.domain.usecase.report

import com.daily.dayo.data.datasource.remote.report.CreateReportPostRequest
import com.daily.dayo.domain.repository.ReportRepository
import javax.inject.Inject

class RequestSavePostReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(body: CreateReportPostRequest) =
        reportRepository.requestSavePostReport(body)
}