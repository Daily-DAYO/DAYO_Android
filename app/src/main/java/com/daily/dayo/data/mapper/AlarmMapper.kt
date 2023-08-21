package com.daily.dayo.data.mapper

import com.daily.dayo.DayoApplication
import com.daily.dayo.common.TimeChangerUtil
import com.daily.dayo.data.datasource.remote.alarm.AlarmDto
import daily.dayo.domain.model.Notification

fun AlarmDto.toNotification(): Notification {
    val textCreatedTime = createdTime?.let {
        TimeChangerUtil.timeChange(
            context = DayoApplication.applicationContext(),
            time = createdTime
        )
    }

    return Notification(
        alarmId = alarmId,
        topic = category,
        check = check,
        content = content,
        createdTime = textCreatedTime,
        image = image,
        nickname = nickname,
        memberId = memberId,
        postId = postId
    )
}