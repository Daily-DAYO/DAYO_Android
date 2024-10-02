package daily.dayo.domain.usecase.report

import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.ReportRepository
import javax.inject.Inject

class RequestSaveCommentReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(comment: String, commentId: Long): NetworkResponse<Void> =
        reportRepository.requestSaveCommentReport(comment = comment, commentId = commentId)
}