package com.daily.dayo.profile.model

import com.google.gson.annotations.SerializedName

data class ResponseMyProfile(
    @SerializedName("followerCount")
    val followerCount: Int,
    @SerializedName("followingCount")
    val followingCount: Int,
    @SerializedName("email")
    val email:String,
    @SerializedName("nickname")
    val nickname:String,
    @SerializedName("postCount")
    val postCount:	Int,
    @SerializedName("profileImg")
    val profileImg:	String
)

data class ResponseOtherProfile(
    @SerializedName("follow")
    val follow: Boolean,
    @SerializedName("followerCount")
    val followerCount: Int,
    @SerializedName("followingCount")
    val followingCount: Int,
    @SerializedName("memberId")
    val memberId:String,
    @SerializedName("nickname")
    val nickname:String,
    @SerializedName("postCount")
    val postCount:	Int,
    @SerializedName("profileImg")
    val profileImg:	String
)