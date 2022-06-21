package com.daily.dayo.data.datasource.remote.alarm

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlarmApiService {

    @GET("/api/v1/alarms")
    suspend fun requestAllAlarmList(): Response<ListAllAlarmResponse>

    @POST("/api/v1/alarms/{alarmId}")
    suspend fun requestIsCheckAlarm(@Path("alarmId") alarmId: Int): Response<Void>
}