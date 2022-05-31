package com.daily.dayo.data.datasource.remote.bookmark

import com.google.gson.annotations.SerializedName

data class CreateBookmarkRequest(
    @SerializedName("postId")
    val postId: Int
)

data class ListAllMyBookmarkPostResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<BookmarkPostDto>
)

data class BookmarkPostDto(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)
