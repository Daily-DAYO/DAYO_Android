package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.heart.MyHeartPostDto
import com.daily.dayo.domain.model.LikePost

fun MyHeartPostDto.toLikePost() : LikePost =
    LikePost(
        postId = postId,
        thumbnailImage = thumbnailImage
    )