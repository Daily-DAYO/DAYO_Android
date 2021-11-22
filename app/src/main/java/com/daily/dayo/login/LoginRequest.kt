package com.daily.dayo.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("accessToken")
    val accessToken : String
)