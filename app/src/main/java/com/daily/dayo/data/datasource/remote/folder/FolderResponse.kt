package com.daily.dayo.data.datasource.remote.folder

import com.daily.dayo.domain.model.Privacy
import com.google.gson.annotations.SerializedName

data class CreateFolderResponse(
    @SerializedName("folderId")
    val id: Int
)

data class ListAllFolderResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<FolderDto>
)

data class ListAllMyFolderResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<FolderDto>
)

data class FolderInfoResponse(
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("postCount")
    val postCount: Int,
    @SerializedName("privacy")
    val privacy: Privacy,
    @SerializedName("subheading")
    var subheading: String?,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)

data class DetailFolderResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val posts: List<FolderPostDto>
)

data class CreateFolderInPostResponse(
    @SerializedName("folderId")
    val folderId: Int
)

data class EditFolderResponse(
    @SerializedName("folderId")
    val folderId: Int
)

data class FolderDto(
    @SerializedName("folderId")
    val folderId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("postCount")
    val postCount: Int,
    @SerializedName("privacy")
    val privacy: Privacy,
    @SerializedName("subheading")
    var subheading: String?,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)

data class FolderPostDto(
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)

data class EditOrderDto(
    @SerializedName("folderId")
    var folderId: Int,
    @SerializedName("orderIndex")
    var orderIndex: Int
)