package com.daily.dayo.post.model

import com.google.gson.annotations.SerializedName

data class ResponsePostComment(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<PostCommentContent>
)

data class PostCommentContent(
    @SerializedName("commentId")
    val commentId: Int,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("createTime")
    val createTime: String,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profileImg: String
)

data class RequestCreatePostComment(
    @SerializedName("contents")
    val contents: String,
    @SerializedName("postId")
    val postId: Int
)

data class ResponseCreatePostComment(
    @SerializedName("commentId")
    val commentId: Int
)
