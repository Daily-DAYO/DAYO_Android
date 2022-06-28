package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.alarm.ListAllAlarmResponse
import retrofit2.Response

interface AlarmRepository {

    suspend fun requestAllAlarmList(): Response<ListAllAlarmResponse>
    suspend fun requestIsCheckAlarm(alarmId: Int): Response<Void>
}