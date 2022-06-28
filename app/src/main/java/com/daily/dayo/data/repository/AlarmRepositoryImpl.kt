package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.alarm.AlarmApiService
import com.daily.dayo.data.datasource.remote.alarm.ListAllAlarmResponse
import com.daily.dayo.domain.repository.AlarmRepository
import retrofit2.Response
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmApiService: AlarmApiService
) : AlarmRepository {

    override suspend fun requestAllAlarmList(): Response<ListAllAlarmResponse> =
        alarmApiService.requestAllAlarmList()

    override suspend fun requestIsCheckAlarm(alarmId: Int): Response<Void> =
        alarmApiService.requestIsCheckAlarm(alarmId)
}