package com.daily.dayo

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import com.kakao.sdk.common.KakaoSdk

@HiltAndroidApp
class DayoApplication : Application(){

    init{
        instance = this
    }

    companion object {
        lateinit var instance: DayoApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.NATIVE_APP_KEY)
    }

}