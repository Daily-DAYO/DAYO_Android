package com.daily.dayo.network.setting

import com.daily.dayo.setting.model.RequestChangePassword
import com.daily.dayo.setting.model.RequestCheckCurrentPassword
import retrofit2.Response

interface SettingApiHelper {
    suspend fun requestCheckCurrentPassword(request: RequestCheckCurrentPassword): Response<Void>
    suspend fun requestChangePassword(request: RequestChangePassword): Response<Void>
}