package daily.dayo.domain.repository

import daily.dayo.domain.model.NetworkResponse

interface ReportRepository {

    suspend fun requestSaveMemberReport(comment: String, memberId: String): NetworkResponse<Void>
    suspend fun requestSavePostReport(comment: String, postId: Long): NetworkResponse<Void>
    suspend fun requestSaveCommentReport(comment: String, commentId: Long): NetworkResponse<Void>
}