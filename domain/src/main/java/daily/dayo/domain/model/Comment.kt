package daily.dayo.domain.model

data class Comment(
    val commentId: Int,
    val contents: String,
    val createTime: String,
    val memberId: String,
    val nickname: String,
    val profileImg: String
)

data class Comments(
    val count: Int,
    val data: List<Comment>
)

data class MentionUser(
    val memberId: String,
    val nickname: String,
    val order: Int
)