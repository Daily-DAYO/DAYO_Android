package com.daily.dayo.write.model

import com.google.gson.annotations.SerializedName

data class RequestWrite (
    @SerializedName("category")
    val category: String,
    @SerializedName("contents")
    val contents : String,
    @SerializedName("files")
    val files : List<String>,
    @SerializedName("folderId")
    val folderId : Int,
    @SerializedName("memberId")
    val memberId : String,
    @SerializedName("privacy")
    val privacy : String,
    @SerializedName("tags")
    val tags : List<String>
)

data class ResponseWrite (
    @SerializedName("id")
    val id: Int
)

data class RequestEditWrite (
    @SerializedName("category")
    val category: String,
    @SerializedName("contents")
    val contents : String,
    @SerializedName("folderId")
    val folderId : Int,
    @SerializedName("hashtags")
    val hashtags : List<String>
)

data class ResponseEditWrite (
    @SerializedName("postId")
    val postId: Int
)