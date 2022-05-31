package com.daily.dayo.data.datasource.remote.heart

import com.google.gson.annotations.SerializedName

data class CreateHeartResponse(
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("postId")
    val postId: Int
)

data class ListAllMyHeartPostResponse(
    @SerializedName("count")
    val count:Int,
    @SerializedName("data")
    val data:List<MyHeartPostDto>
)

data class MyHeartPostDto(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)