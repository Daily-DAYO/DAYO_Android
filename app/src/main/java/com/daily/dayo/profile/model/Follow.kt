package com.daily.dayo.profile.model

import com.google.gson.annotations.SerializedName

data class ResponseCreateFollow(
    @SerializedName("followId")
    val followId: String,
    @SerializedName("isAccept")
    val isAccept: Boolean,
    @SerializedName("memberId")
    val memberId: String
)

data class ResponseListAllFollow(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<FollowInfo>
)

data class FollowInfo(
    @SerializedName("isFollow")
    val isFollow: Boolean,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profile: String
)

data class RequestCreateFollow(
    @SerializedName("followerId")
    val followerId: String
)