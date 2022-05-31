package com.daily.dayo.data.datasource.remote.bookmark

import com.google.gson.annotations.SerializedName

data class CreateBookmarkResponse(
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("postId")
    val postId: Int
)