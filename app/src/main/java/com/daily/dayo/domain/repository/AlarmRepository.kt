package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.alarm.ListAllAlarmResponse
import com.daily.dayo.domain.model.NetworkResponse

interface AlarmRepository {

    suspend fun requestAllAlarmList(): NetworkResponse<ListAllAlarmResponse>
    suspend fun requestIsCheckAlarm(alarmId: Int): NetworkResponse<Void>
}