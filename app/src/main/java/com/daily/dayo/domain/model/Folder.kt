package com.daily.dayo.domain.model

import android.graphics.Bitmap

data class Folder(
    val folderId: Int?,
    val name: String,
    val memberId: String?,
    val privacy: Privacy,
    val subheading: String?,
    val thumbnailImage: String,
    val postCount: Int,
    val posts: List<FolderPost>?
)

data class FolderPost(
    val postId: Int,
    val thumbnailImage: String,
    var preLoadThumbnail: Bitmap? = null
)

data class FolderOrder(
    var folderId: Int,
    var orderIndex: Int
)