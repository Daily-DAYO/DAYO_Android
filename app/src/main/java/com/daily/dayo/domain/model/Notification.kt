package com.daily.dayo.domain.model

data class Notification(
    val alarmId:Int?,
    val topic: Topic?,
    val check: Boolean?,
    val content: String?,
    val createdTime: String?,
    val image: String?,
    val nickname: String?,
    val memberId: String?,
    val postId: Int?
)