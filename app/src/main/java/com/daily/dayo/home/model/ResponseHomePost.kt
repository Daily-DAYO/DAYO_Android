package com.daily.dayo.home.model

import com.google.gson.annotations.SerializedName

data class ResponseHomePost (
    @SerializedName("posts")
    val posts: List<Post>
)

data class Post(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<PostContent>
)

data class PostContent(
    @SerializedName("commentCount")
    val commentCount: Int,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
    @SerializedName("userProfileImage")
    val userProfileImage: String
)