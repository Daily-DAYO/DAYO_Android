package com.daily.dayo.data.mapper

import com.daily.dayo.DayoApplication
import com.daily.dayo.common.TimeChangerUtil
import com.daily.dayo.data.datasource.remote.comment.CommentDto
import com.daily.dayo.data.datasource.remote.comment.ListAllCommentResponse
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.Comments

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

fun ListAllCommentResponse.toComments(): Comments {
    return Comments(
        count = count,
        data = data.map { it.toComment() }
    )
}