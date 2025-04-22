package daily.dayo.domain.model

data class BookmarkPost(
    val postId: Long,
    val thumbnailImage: String
)
data class BookmarkPostResponse(
    val memberId: String,
    val postId: Long
)