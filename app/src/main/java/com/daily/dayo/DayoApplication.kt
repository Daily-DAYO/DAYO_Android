package com.daily.dayo

import android.app.Application
import android.content.Context
import com.bumptech.glide.Glide
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
        val cacheDirPath get () = applicationContext().cacheDir.toString()
    }

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.NATIVE_APP_KEY)
        preferences = SharedManager(applicationContext)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).trimMemory(level)
    }
}