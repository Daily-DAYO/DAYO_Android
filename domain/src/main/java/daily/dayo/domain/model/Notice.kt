package daily.dayo.domain.model

import java.io.Serializable

data class Notice(
    val noticeId: Int,
    val title: String,
    val uploadDate: String
) : Serializable

data class NoticeDetail(
    val contents: String
)