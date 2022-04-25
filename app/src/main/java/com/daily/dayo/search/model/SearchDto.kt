package com.daily.dayo.search.model

import com.google.gson.annotations.SerializedName

data class RequestSearchTag(
    @SerializedName("tag")
    val tag: String,
)

data class ResponseSearchTag(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<SearchTagPostContent>,
)

data class SearchTagPostContent(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
)
