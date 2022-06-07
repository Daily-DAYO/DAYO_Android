package com.daily.dayo.data.datasource.remote.search

import com.google.gson.annotations.SerializedName

data class SearchResultResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<SearchDto>,
)

data class SearchDto(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
)
