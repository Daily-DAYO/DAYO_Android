package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.alarm.AlarmDto
import daily.dayo.domain.model.Notification

fun AlarmDto.toNotification(): Notification {
    return Notification(
        alarmId = alarmId,
        topic = category,
        check = check,
        content = content,
        createdTime = createdTime,
        image = image,
        nickname = nickname,
        memberId = memberId,
        postId = postId,
        profileImage = profileImage,
    )
}