package com.daily.dayo.domain.model

data class Folder(
    val folderId: Int?,
    val name: String,
    val memberId: String?,
    val privacy: Privacy,
    val subheading:	String?,
    val thumbnailImage:	String,
    val postCount:Int,
    val posts: List<FolderPost>?
)

data class FolderPost(
    val postId: Int,
    val thumbnailImage: String
)

data class FolderOrder(
    var folderId: Int,
    var orderIndex: Int
)