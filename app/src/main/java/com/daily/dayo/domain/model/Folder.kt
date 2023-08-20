package com.daily.dayo.domain.model

data class Folder(
    val folderId: Int?,
    val title: String,
    val memberId: String?,
    val privacy: Privacy,
    val subheading: String?,
    val thumbnailImage: String,
    val postCount: Int
)

data class Folders(
    val count: Int,
    val data: List<Folder>
)
data class FoldersMine(
    val count: Int,
    val data: List<Folder>
)

data class FolderPost(
    val createDate: String,
    val postId: Int,
    val thumbnailImage: String
)

data class FolderOrder(
    var folderId: Int,
    var orderIndex: Int
)

data class FolderCreateResponse(
    val folderId: Int
)

data class FolderCreateInPostResponse(
    val folderId: Int
)

data class FolderEditResponse(
    val folderId: Int
)

data class FolderInfo(
    val memberId: String,
    val name: String,
    val postCount: Int,
    val privacy: Privacy,
    var subheading: String?,
    val thumbnailImage: String
)