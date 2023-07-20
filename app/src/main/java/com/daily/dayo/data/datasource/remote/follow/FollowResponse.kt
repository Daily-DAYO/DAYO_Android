package com.daily.dayo.data.datasource.remote.follow

import com.google.gson.annotations.SerializedName

data class CreateFollowResponse(
    @SerializedName("followId")
    val followId: String,
    @SerializedName("isAccept")
    val isAccept: Boolean,
    @SerializedName("memberId")
    val memberId: String
)
data class ListAllFollowerResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<MyFollowerDto>
)

data class ListAllFollowingResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<MyFollowerDto>
)

data class CreateFollowUpResponse(
    @SerializedName("followId")
    val followId: String,
    @SerializedName("isAccept")
    val isAccept: Boolean,
    @SerializedName("memberId")
    val memberId: String
)

data class MyFollowerDto(
    @SerializedName("isFollow")
    val isFollow: Boolean,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profileImg: String
)