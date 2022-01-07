package com.daily.dayo.post.model

import com.google.gson.annotations.SerializedName

data class ResponsePost (
    @SerializedName("category")
    val category: String,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("createdDateTime")
    val createdDateTime: String,
    @SerializedName("hashtags")
    val hashtags: List<String>,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profileImg: String
    )