package com.daily.dayo.data.datasource.remote.member

import com.google.gson.annotations.SerializedName

data class MemberInfoResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profileImg: String
)

data class MemberOAuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class MemberSignInResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class RefreshTokenResponse(
    @SerializedName("accessToken")
    val accessToken: String
)

data class MemberSignupResponse(
    @SerializedName("memberId")
    val memberId: String
)

data class MemberAuthCodeResponse(
    @SerializedName("authCode")
    val authCode: String
)

data class MemberMyProfileResponse(
    @SerializedName("followerCount")
    val followerCount: Int,
    @SerializedName("followingCount")
    val followingCount: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("postCount")
    val postCount: Int,
    @SerializedName("profileImg")
    val profileImg: String
)

data class MemberOtherProfileResponse(
    @SerializedName("follow")
    val follow: Boolean,
    @SerializedName("followerCount")
    val followerCount: Int,
    @SerializedName("followingCount")
    val followingCount: Int,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("postCount")
    val postCount: Int,
    @SerializedName("profileImg")
    val profileImg: String
)