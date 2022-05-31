package com.daily.dayo.data.datasource.remote.comment

import com.google.gson.annotations.SerializedName

data class CreateCommentResponse(
    @SerializedName("commentId")
    val commentId: Int
)

data class ListAllCommentResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<CommentDto>
)

data class CommentDto(
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