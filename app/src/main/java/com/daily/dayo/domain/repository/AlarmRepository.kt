package com.daily.dayo.domain.repository

import androidx.paging.PagingData
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    suspend fun requestAllAlarmList(): Flow<PagingData<Notification>>
    suspend fun requestIsCheckAlarm(alarmId: Int): NetworkResponse<Void>
}