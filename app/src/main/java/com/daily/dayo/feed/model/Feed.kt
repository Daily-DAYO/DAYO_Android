package com.daily.dayo.feed.model

import com.daily.dayo.post.model.PostCommentContent
import com.google.gson.annotations.SerializedName

data class ResponseFeedList(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<FeedContent>

)

data class FeedContent(
    @SerializedName("commentCount")
    val commentCount: Int,
    @SerializedName("comments")
    val comments: PostCommentContent,
    @SerializedName("heart")
    val heart: Boolean,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
    @SerializedName("userProfileImage")
    val userProfileImage: String
)