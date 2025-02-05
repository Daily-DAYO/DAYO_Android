package daily.dayo.domain.model

data class LikePost(
    val postId: Int,
    val thumbnailImage: String
)

data class LikePostResponse(
    val allCount: Int
)