package com.daily.dayo.domain.usecase.notification

import com.daily.dayo.domain.repository.AlarmRepository
import javax.inject.Inject

class RequestAllAlarmListUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke() =
        alarmRepository.requestAllAlarmList()
}