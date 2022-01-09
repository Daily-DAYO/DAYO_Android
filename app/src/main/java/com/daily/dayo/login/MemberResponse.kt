package com.daily.dayo.login

import com.google.gson.annotations.SerializedName

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