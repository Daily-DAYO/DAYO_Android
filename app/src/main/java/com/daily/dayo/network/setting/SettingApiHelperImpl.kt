package com.daily.dayo.network.setting

import com.daily.dayo.setting.model.RequestChangePassword
import com.daily.dayo.setting.model.RequestCheckCurrentPassword
import retrofit2.Response
import javax.inject.Inject

class SettingApiHelperImpl @Inject constructor(private val settingApiService: SettingApiService) :
    SettingApiHelper {
    override suspend fun requestCheckCurrentPassword(request: RequestCheckCurrentPassword): Response<Void> =
        settingApiService.requestCheckCurrentPassword(request)

    override suspend fun requestChangePassword(request: RequestChangePassword): Response<Void> =
        settingApiService.requestChangePassword(request)
}