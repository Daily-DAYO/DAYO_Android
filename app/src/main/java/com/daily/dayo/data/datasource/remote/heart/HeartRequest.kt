package com.daily.dayo.data.datasource.remote.heart

import com.google.gson.annotations.SerializedName

data class CreateHeartRequest(
    @SerializedName("postId")
    val postId: Int
)