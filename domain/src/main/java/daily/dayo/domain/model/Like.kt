package daily.dayo.domain.model

data class LikePost(
    val postId: Int,
    val thumbnailImage: String
)

data class LikePostResponse(
    val memberId: String,
    val postId: Int,
    val allCount: Int
)

data class LikePostDeleteResponse(
    val allCount: Int
)