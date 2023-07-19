package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.notice.NoticeDto
import com.daily.dayo.domain.model.Notice
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