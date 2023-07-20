package com.daily.dayo.domain.model

data class Comment(
    val commentId: Int,
    val contents: String,
    val createTime: String,
    val memberId: String,
    val nickname: String,
    val profileImg: String
)