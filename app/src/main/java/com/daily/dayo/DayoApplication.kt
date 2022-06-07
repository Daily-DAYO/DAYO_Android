package com.daily.dayo

import android.app.Application
import android.content.Context
import com.daily.dayo.data.datasource.local.SharedManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DayoApplication : Application(){

    init{
        instance = this
    }

    companion object {
        lateinit var instance: DayoApplication
        lateinit var preferences : SharedManager
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.NATIVE_APP_KEY)
        preferences = SharedManager(applicationContext)
    }
}