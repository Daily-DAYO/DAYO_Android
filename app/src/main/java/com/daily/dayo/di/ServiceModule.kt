package com.daily.dayo.di

import com.daily.dayo.network.home.HomeApiHelper
import com.daily.dayo.network.home.HomeApiHelperImpl
import com.daily.dayo.network.home.HomeApiService
import com.daily.dayo.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideHomeApiService(retrofit: Retrofit) = retrofit.create(HomeApiService::class.java)

    @Singleton
    @Provides
    fun provideHomeApiHelper(homeApiHelper: HomeApiHelper): HomeApiHelper = homeApiHelper

    @Singleton
    @Provides
    fun provideHomeRepository(homeApiHelperImpl: HomeApiHelperImpl) : HomeRepository =
        HomeRepository(homeApiHelperImpl)
}