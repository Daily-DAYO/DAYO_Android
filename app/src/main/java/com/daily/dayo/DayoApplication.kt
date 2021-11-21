package com.daily.dayo

import android.app.Application
import com.daily.dayo.login.LoginRepository
import com.daily.dayo.login.LoginServiceImpl
import com.kakao.sdk.common.KakaoSdk

class DayoApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.NATIVE_APP_KEY)
    }
}