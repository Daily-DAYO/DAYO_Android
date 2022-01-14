package com.daily.dayo.profile.model

import com.google.gson.annotations.SerializedName

data class Folder(
    @SerializedName("folderId")
    val folderId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("postCount")
    val postCount:Int,
    @SerializedName("subheading")
    val subheading:	String,
    @SerializedName("thumbnailImage")
    val thumbnailImage:	String
)

data class ResponseAllFolderList(
    @SerializedName("count")
    val count:Int,
    @SerializedName("data")
    val data: List<Folder>
)
data class ResponseAllMyFolderList(
    @SerializedName("count")
    val count:Int,
    @SerializedName("data")
    val data: List<Folder>
)

data class ResponseFolderId(
    @SerializedName("folderId")
    val id:Int
)

data class RequestCreateFolderInPost(
    @SerializedName("name")
    val name:String,
    @SerializedName("privacy")
    val privacy:String
)

