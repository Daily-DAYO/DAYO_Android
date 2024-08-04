package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.comment.CommentDto
import daily.dayo.data.datasource.remote.comment.ListAllCommentResponse
import daily.dayo.data.datasource.remote.comment.MentionUserDto
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.MentionUser

fun CommentDto.toComment(): Comment = Comment(
    commentId = commentId,
    contents = contents,
    createTime = createTime,
    memberId = memberId,
    mentionList = mentionList.map { it.toMentionUser() },
    nickname = nickname,
    profileImg = profileImg
)

fun ListAllCommentResponse.toComments(): Comments {
    return Comments(
        count = count,
        data = data.map { it.toComment() }
    )
}

fun MentionUserDto.toMentionUser(): MentionUser {
    return MentionUser(
        memberId = memberId,
        nickname = nickname,
        order = order
    )
}