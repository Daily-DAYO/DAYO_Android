package daily.dayo.data.repository

import daily.dayo.data.datasource.remote.report.CreateReportCommentRequest
import daily.dayo.data.datasource.remote.report.CreateReportMemberRequest
import daily.dayo.data.datasource.remote.report.CreateReportPostRequest
import daily.dayo.data.datasource.remote.report.ReportApiService
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.ReportRepository
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

    override suspend fun requestSaveCommentReport(
        comment: String,
        commentId: Int
    ): NetworkResponse<Void> =
        reportApiService.requestSaveCommentReport(
            CreateReportCommentRequest(
                comment = comment,
                commentId = commentId
            )
        )
}