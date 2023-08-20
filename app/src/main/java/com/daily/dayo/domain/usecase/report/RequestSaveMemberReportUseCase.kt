package com.daily.dayo.domain.usecase.report

import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.ReportRepository
import javax.inject.Inject

class RequestSaveMemberReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(comment: String, memberId: String): NetworkResponse<Void> =
        reportRepository.requestSaveMemberReport(comment = comment, memberId = memberId)
}