package daily.dayo.domain.model

data class LikeUser(
    val follow: Boolean,
    val memberId: String,
    val nickname: String,
    val profileImg: String,
)