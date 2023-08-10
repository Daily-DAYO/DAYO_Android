package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.heart.HeartMemberDto
import com.daily.dayo.data.datasource.remote.heart.MyHeartPostDto
import com.daily.dayo.domain.model.LikePost
import com.daily.dayo.domain.model.LikeUser

fun MyHeartPostDto.toLikePost() : LikePost =
    LikePost(
        postId = postId,
        thumbnailImage = thumbnailImage
    )

fun HeartMemberDto.toLikeUser() : LikeUser =
    LikeUser(
        follow = follow,
        nickname = nickname,
        memberId = memberId,
        profileImg = profileImg
    )