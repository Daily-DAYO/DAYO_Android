package daily.dayo.domain.model

data class Comment(
    val commentId: Long,
    val memberId: String,
    val nickname: String,
    val profileImg: String,
    val contents: String,
    val createTime: String,
    val replyList: List<Comment>,
    val mentionList: List<MentionUser>,
)

data class Comments(
    val count: Int,
    val data: List<Comment>
)

data class MentionUser(
    val memberId: String,
    val nickname: String
)