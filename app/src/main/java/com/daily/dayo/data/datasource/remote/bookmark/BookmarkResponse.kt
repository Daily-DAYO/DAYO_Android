package com.daily.dayo.data.datasource.remote.bookmark

import com.google.gson.annotations.SerializedName

data class CreateBookmarkResponse(
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("postId")
    val postId: Int
)

data class ListAllMyBookmarkPostResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val data: List<BookmarkPostDto>
)

data class BookmarkPostDto(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)
