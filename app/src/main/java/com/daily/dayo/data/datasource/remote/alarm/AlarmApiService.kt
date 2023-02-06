package com.daily.dayo.data.datasource.remote.alarm

import com.daily.dayo.domain.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlarmApiService {

    @GET("/api/v1/alarms")
    suspend fun requestAllAlarmList(): NetworkResponse<ListAllAlarmResponse>

    @POST("/api/v1/alarms/{alarmId}")
    suspend fun requestIsCheckAlarm(@Path("alarmId") alarmId: Int): NetworkResponse<Void>
}