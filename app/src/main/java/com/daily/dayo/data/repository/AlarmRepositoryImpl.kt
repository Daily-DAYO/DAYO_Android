package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.alarm.AlarmApiService
import com.daily.dayo.data.datasource.remote.alarm.ListAllAlarmResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.AlarmRepository
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmApiService: AlarmApiService
) : AlarmRepository {

    override suspend fun requestAllAlarmList(): NetworkResponse<ListAllAlarmResponse> =
        alarmApiService.requestAllAlarmList()

    override suspend fun requestIsCheckAlarm(alarmId: Int): NetworkResponse<Void> =
        alarmApiService.requestIsCheckAlarm(alarmId)
}