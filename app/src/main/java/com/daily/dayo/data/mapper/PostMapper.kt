package com.daily.dayo.data.mapper

import com.daily.dayo.DayoApplication
import com.daily.dayo.common.TimeChangerUtil
import com.daily.dayo.data.datasource.remote.post.CreatePostResponse
import com.daily.dayo.data.datasource.remote.post.DayoPick
import com.daily.dayo.data.datasource.remote.post.DayoPickPostListResponse
import com.daily.dayo.data.datasource.remote.post.DetailPostResponse
import com.daily.dayo.data.datasource.remote.post.EditPostResponse
import com.daily.dayo.data.datasource.remote.post.FeedDto
import com.daily.dayo.data.datasource.remote.post.ListAllPostResponse
import com.daily.dayo.data.datasource.remote.post.ListCategoryPostResponse
import com.daily.dayo.data.datasource.remote.post.PostDto
import daily.dayo.domain.model.Post
import daily.dayo.domain.model.PostCreateResponse
import daily.dayo.domain.model.PostDetail
import daily.dayo.domain.model.PostEditResponse
import daily.dayo.domain.model.Posts
import daily.dayo.domain.model.PostsCategorized
import daily.dayo.domain.model.PostsDayoPick

fun CreatePostResponse.toPostCreateResponse(): PostCreateResponse =
    PostCreateResponse(id = id)

fun EditPostResponse.toPostEditResponse(): PostEditResponse =
    PostEditResponse(postId = postId)

fun ListAllPostResponse.toPosts(): Posts =
    Posts(count = count, data = data.map { it.toPost() })

fun ListCategoryPostResponse.toPostsCategorized(): PostsCategorized =
    PostsCategorized(count = count, data = data.map { it.toPost() })

fun DayoPickPostListResponse.toPostsDayoPick(): PostsDayoPick =
    PostsDayoPick(count = count, data = data.map { it.toPost() })

fun PostDto.toPost(): Post = Post(
    postId = postId,
    memberId = memberId,
    nickname = nickname,
    userProfileImage = userProfileImage,
    category = null,
    thumbnailImage = thumbnailImage,
    postImages = null,
    contents = null,
    createDateTime = null,
    commentCount = commentCount,
    comments = null,
    hashtags = null,
    bookmark = null,
    heart = heart,
    heartCount = heartCount,
    folderId = null,
    folderName = null
)

fun DayoPick.toPost(): Post =
    Post(
        postId = postId,
        memberId = memberId,
        nickname = nickname,
        userProfileImage = userProfileImage,
        category = null,
        thumbnailImage = thumbnailImage,
        postImages = null,
        contents = null,
        createDateTime = null,
        commentCount = commentCount,
        comments = null,
        hashtags = null,
        bookmark = null,
        heart = heart,
        heartCount = heartCount,
        folderId = null,
        folderName = null
    )

fun FeedDto.toPost(): Post {
    val createDateTime = TimeChangerUtil.timeChange(
        context = DayoApplication.applicationContext(),
        time = createTime
    )

    return Post(
        postId = postId,
        memberId = memberId,
        nickname = nickname ?: "null",
        userProfileImage = userProfileImage,
        category = category,
        thumbnailImage = thumbnailImage,
        postImages = null,
        contents = contents,
        createDateTime = createDateTime,
        commentCount = commentCount,
        comments = null,
        hashtags = hashtags,
        bookmark = bookmark,
        heart = heart,
        heartCount = heartCount,
        folderId = null,
        folderName = null
    )
}

fun DetailPostResponse.toPostDetail(): PostDetail {
    val createDateTime = TimeChangerUtil.timeChange(
        context = DayoApplication.applicationContext(),
        time = createDateTime
    )

    return PostDetail(
        bookmark = bookmark,
        category = category,
        contents = contents,
        createDateTime = createDateTime,
        folderId = folderId,
        folderName = folderName,
        hashtags = hashtags,
        heart = heart,
        heartCount = heartCount,
        images = images,
        memberId = memberId,
        nickname = nickname,
        profileImg = profileImg
    )
}

fun DetailPostResponse.toPost(): Post {
    val createDateTime = TimeChangerUtil.timeChange(
        context = DayoApplication.applicationContext(),
        time = createDateTime
    )

    return Post(
        postId = null,
        memberId = memberId,
        nickname = nickname,
        userProfileImage = profileImg,
        category = category,
        thumbnailImage = null,
        postImages = images,
        contents = contents,
        createDateTime = createDateTime,
        commentCount = null,
        comments = null,
        hashtags = hashtags,
        bookmark = bookmark,
        heart = heart,
        heartCount = heartCount,
        folderId = folderId,
        folderName = folderName
    )
}