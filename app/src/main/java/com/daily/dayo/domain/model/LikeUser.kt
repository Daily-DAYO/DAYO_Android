package com.daily.dayo.domain.model

data class LikeUser(
    val follow: Boolean,
    val memberId: Int,
    val nickname: String,
    val thumbnailImage: String,
)