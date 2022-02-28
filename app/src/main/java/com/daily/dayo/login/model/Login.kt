package com.daily.dayo.login.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("accessToken")
    val accessToken : String
)

data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken : String,
    @SerializedName("refreshToken")
    val refreshToken : String
)

data class MemberResponse (
    @SerializedName("email")
    val email : String,
    @SerializedName("memberId")
    val memberId : String,
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("profileImg")
    val profileImg : String
)

data class ResponseRefreshToken(
    @SerializedName("accessToken")
    val accessToken: String
)