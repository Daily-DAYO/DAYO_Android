package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.notice.NoticeDetailResponse
import daily.dayo.data.datasource.remote.notice.NoticeDto
import daily.dayo.domain.model.Notice
import daily.dayo.domain.model.NoticeDetail
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NoticeDto.toNotice(): Notice {
    val textCreatedTime = createdDate.let {
        val noticeDateFormat = DateTimeFormatter.ofPattern("yyyy. MM. dd")
        LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(noticeDateFormat)
    }

    return Notice(
        noticeId = id,
        title = title,
        uploadDate = textCreatedTime,
    )
}

fun NoticeDetailResponse.toNoticeDetail() =
    NoticeDetail(
        contents = contents
    )