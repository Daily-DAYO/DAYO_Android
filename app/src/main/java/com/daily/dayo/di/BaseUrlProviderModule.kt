package com.daily.dayo.di

import android.content.Context
import com.daily.dayo.config.RemoteConfigBaseUrlProvider
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import daily.dayo.domain.provider.BaseUrlProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object BaseUrlProviderModule {

    @Singleton
    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Singleton
    @Provides
    fun provideBaseUrlProvider(
        @ApplicationContext context: Context,
        remoteConfig: FirebaseRemoteConfig
    ): BaseUrlProvider = RemoteConfigBaseUrlProvider(
        context = context,
        remoteConfig = remoteConfig
    )
}
