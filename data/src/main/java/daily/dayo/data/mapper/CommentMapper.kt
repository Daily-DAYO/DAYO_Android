package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.comment.CommentDto
import daily.dayo.data.datasource.remote.comment.ListAllCommentResponse
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.Comments

fun CommentDto.toComment(): Comment = Comment(
    commentId = commentId,
    contents = contents,
    createTime = createTime,
    memberId = memberId,
    nickname = nickname,
    profileImg = profileImg
)

fun ListAllCommentResponse.toComments(): Comments {
    return Comments(
        count = count,
        data = data.map { it.toComment() }
    )
}