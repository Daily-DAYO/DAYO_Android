package com.daily.dayo.repository

import com.daily.dayo.SharedManager
import com.daily.dayo.network.setting.SettingApiHelper
import com.daily.dayo.setting.model.RequestChangePassword
import com.daily.dayo.setting.model.RequestCheckCurrentPassword
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val settingApiHelper: SettingApiHelper,
    private val sharedManager: SharedManager,
) {
    suspend fun requestCheckCurrentPassword(inputPassword: String) =
        settingApiHelper.requestCheckCurrentPassword(
            RequestCheckCurrentPassword(
                inputPassword
            )
        )

    suspend fun requestChangePassword(
        email: String = sharedManager.getCurrentUser().email.toString(), newPassword: String,
    ) = settingApiHelper.requestChangePassword(RequestChangePassword(email, newPassword))
}