package com.daily.dayo.domain.model

data class Post(
    val postId: Int?,
    val memberId: String?,
    val nickname: String,
    val userProfileImage: String,
    val category: Category?,
    val thumbnailImage: String?,
    val postImages: List<String>?,
    val contents: String?,
    val createDateTime: String?,
    val commentCount: Int?,
    val comments: List<Comment>?,
    val hashtags: List<String>?,
    val bookmark: Boolean?,
    val heart: Boolean,
    val heartCount: Int,
    val folderId: Int?,
    val folderName: String?
)