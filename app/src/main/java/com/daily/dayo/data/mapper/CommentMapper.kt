package com.daily.dayo.data.mapper

import com.daily.dayo.DayoApplication
import com.daily.dayo.common.TimeChangerUtil
import com.daily.dayo.data.datasource.remote.comment.CommentDto
import com.daily.dayo.domain.model.Comment

fun CommentDto.toComment(): Comment {
    val createDateTime = TimeChangerUtil.timeChange(
        context = DayoApplication.applicationContext(),
        time = createTime
    )

    return Comment(
        commentId = commentId,
        contents = contents,
        createTime = createDateTime,
        memberId = memberId,
        nickname = nickname,
        profileImg = profileImg
    )
}