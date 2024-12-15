package daily.dayo.domain.model

data class Follow(
    val isFollow: Boolean,
    val memberId: String,
    val nickname: String,
    val profileImg: String
)

data class Following(
    val count: Int,
    val data: List<Follow>
)

data class Follower(
    val count: Int,
    val data: List<Follow>
)

data class FollowCreateResponse(
    val followerId: String,
    val isAccept: Boolean,
    val memberId: String
)

data class FollowUpCreateResponse(
    val followId: String,
    val isAccept: Boolean,
    val memberId: String
)