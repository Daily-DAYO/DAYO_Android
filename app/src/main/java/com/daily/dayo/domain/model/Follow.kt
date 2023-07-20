package com.daily.dayo.domain.model

data class Follow(
    val isFollow: Boolean,
    val memberId: String,
    val nickname: String,
    val profileImg: String
)
