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

data class FolderPost(
    val createDate: String,
    val postId: Int,
    val thumbnailImage: String
)

data class FolderOrder(
    var folderId: Int,
    var orderIndex: Int
)