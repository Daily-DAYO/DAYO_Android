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
    @SerializedName("heart")
    val heart: Boolean,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profileImg: String
)

data class RequestLikePost(
    @SerializedName("postId")
    val postId: Int
)

data class ResponseLikePost(
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("postId")
    val postId: Int
)