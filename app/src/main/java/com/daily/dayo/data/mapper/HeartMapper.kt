package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.heart.CreateHeartResponse
import com.daily.dayo.data.datasource.remote.heart.DeleteHeartResponse
import com.daily.dayo.data.datasource.remote.heart.HeartMemberDto
import com.daily.dayo.data.datasource.remote.heart.MyHeartPostDto
import daily.dayo.domain.model.LikePost
import daily.dayo.domain.model.LikePostDeleteResponse
import daily.dayo.domain.model.LikePostResponse
import daily.dayo.domain.model.LikeUser

fun CreateHeartResponse.toLikePostResponse(): LikePostResponse =
    LikePostResponse(memberId = memberId, postId = postId, allCount = allCount)

fun DeleteHeartResponse.toLikePostDeleteResponse(): LikePostDeleteResponse =
    LikePostDeleteResponse(allCount = allCount)

fun MyHeartPostDto.toLikePost(): LikePost =
    LikePost(
        postId = postId,
        thumbnailImage = thumbnailImage
    )

fun HeartMemberDto.toLikeUser(): LikeUser =
    LikeUser(
        follow = follow,
        nickname = nickname,
        memberId = memberId,
        profileImg = profileImg
    )