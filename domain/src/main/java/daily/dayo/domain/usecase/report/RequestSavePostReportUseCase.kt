package daily.dayo.domain.usecase.report

import daily.dayo.domain.repository.ReportRepository
import javax.inject.Inject

class RequestSavePostReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(comment: String, postId: Int) =
        reportRepository.requestSavePostReport(comment = comment, postId = postId)
}