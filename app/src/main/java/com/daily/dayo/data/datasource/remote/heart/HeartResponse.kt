package com.daily.dayo.data.datasource.remote.heart

import com.google.gson.annotations.SerializedName

data class CreateHeartResponse(
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("allCount")
    val allCount: Int
)

data class DeleteHeartResponse(
    @SerializedName("allCount")
    val allCount: Int
)

data class ListAllMyHeartPostResponse(
    @SerializedName("count")
    val count:Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val data:List<MyHeartPostDto>
)

data class MyHeartPostDto(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)

data class PostMemberHeartListResponse(
    @SerializedName("count")
    val count:Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val data:List<HeartMemberDto>
)

data class HeartMemberDto(
    @SerializedName("follow")
    val follow: Boolean,
    @SerializedName("memberId")
    val memberId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)