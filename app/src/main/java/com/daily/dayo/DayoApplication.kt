package com.daily.dayo

import android.app.Application
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DayoApplication : Application(){
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.NATIVE_APP_KEY)
        firebaseAnalytics = Firebase.analytics
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