package com.daily.dayo.profile.model

import com.google.gson.annotations.SerializedName

data class ResponseAllFolderList(
    @SerializedName("count")
    val count:Int,
    @SerializedName("data")
    val data: List<Folder>
)

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

data class ResponseCreateFolder(
    @SerializedName("folderId")
    val id:Int
)