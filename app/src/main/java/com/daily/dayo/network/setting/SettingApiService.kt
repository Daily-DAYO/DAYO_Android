package com.daily.dayo.network.setting

import com.daily.dayo.setting.model.RequestChangePassword
import com.daily.dayo.setting.model.RequestCheckCurrentPassword
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SettingApiService {
    @POST("/api/v1/members/checkPassword")
    suspend fun requestCheckCurrentPassword(@Body body: RequestCheckCurrentPassword): Response<Void>

    @POST("/api/v1/members/setting/changePassword")
    suspend fun requestChangePassword(@Body body: RequestChangePassword): Response<Void>
}