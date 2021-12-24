package com.daily.dayo.profile.model

import com.google.gson.annotations.SerializedName

data class RequestCreateFolder(
    @SerializedName("name")
    val name: String,
    @SerializedName("subheading")
    val subheading: String,
    @SerializedName("memberId")
    val memberId: String?
)
