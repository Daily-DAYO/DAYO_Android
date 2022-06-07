package com.daily.dayo.data.mapper

import com.daily.dayo.DayoApplication
import com.daily.dayo.common.TimeChangerUtil
import com.daily.dayo.data.datasource.remote.post.DayoPick
import com.daily.dayo.data.datasource.remote.post.DetailPostResponse
import com.daily.dayo.data.datasource.remote.post.FeedDto
import com.daily.dayo.data.datasource.remote.post.PostDto
import com.daily.dayo.domain.model.Post

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
        time = localDateTime
    )
    val comments = comments.map { it.toComment() }

    return Post(
        postId = postId,
        memberId = memberId,
        nickname = nickname,
        userProfileImage = userProfileImage,
        category = category,
        thumbnailImage = thumbnailImage,
        postImages = null,
        contents = contents,
        createDateTime = createDateTime,
        commentCount = commentCount,
        comments = comments,
        hashtags = hashtags,
        bookmark = bookmark,
        heart = heart,
        heartCount = heartCount,
        folderId = null,
        folderName = null
    )
}

fun DetailPostResponse.toPost() : Post {
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