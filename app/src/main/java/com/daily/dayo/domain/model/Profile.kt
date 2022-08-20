package com.daily.dayo.domain.model

data class Profile(
    val memberId: String,
    val email: String,
    val nickname: String,
    val profileImg: String,
    val postCount: Int,
    val followerCount: Int,
    val followingCount: Int,
    val follow: Boolean,
)