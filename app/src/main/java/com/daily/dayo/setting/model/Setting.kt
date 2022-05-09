package com.daily.dayo.setting.model

import com.google.gson.annotations.SerializedName

data class RequestCheckCurrentPassword(
    @SerializedName("password")
    val password: String,
)

data class RequestChangePassword(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)