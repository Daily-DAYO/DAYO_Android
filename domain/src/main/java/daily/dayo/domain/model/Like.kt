package daily.dayo.domain.model

data class LikePost(
    val postId: Long,
    val thumbnailImage: String
)

data class LikePostResponse(
    val allCount: Int
)