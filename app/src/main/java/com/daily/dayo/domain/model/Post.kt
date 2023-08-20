package com.daily.dayo.domain.model

data class Post(
    val postId: Int?,
    val memberId: String?,
    val nickname: String,
    val userProfileImage: String,
    val category: Category?,
    var thumbnailImage: String?,
    val postImages: List<String>?,
    val contents: String?,
    val createDateTime: String?,
    val commentCount: Int?,
    val comments: List<Comment>?,
    val hashtags: List<String>?,
    var bookmark: Boolean?,
    var heart: Boolean,
    var heartCount: Int,
    val folderId: Int?,
    val folderName: String?,
    var preLoadThumbnail: ByteArray?=null,
    var preLoadUserImg: ByteArray?=null
)

data class PostDetail(
    var bookmark: Boolean?,
    val category: Category,
    val contents: String,
    val createDateTime: String,
    val folderId: Int,
    val folderName: String,
    val hashtags: List<String>,
    var heart: Boolean,
    var heartCount: Int,
    val images: List<String>,
    val memberId: String,
    val nickname: String,
    val profileImg: String
)

data class PostCreateResponse(
    val id: Int
)

data class PostEditResponse(
    val postId: Int
)

data class Posts(
    val count: Int,
    val data: List<Post>
)

data class PostsCategorized(
    val count: Int,
    val data: List<Post>
)

data class PostsDayoPick(
    val count: Int,
    val data: List<Post>
)
data class PostsNew(
    val count: Int,
    val data: List<Post>
)