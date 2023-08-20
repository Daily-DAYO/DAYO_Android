package com.daily.dayo.domain.model

data class BookmarkPost(
    val postId: Int,
    val thumbnailImage: String
)
data class BookmarkPostResponse(
    val memberId: String,
    val postId: Int
)