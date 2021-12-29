package com.daily.dayo.di

import com.daily.dayo.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient() = if(BuildConfig.DEBUG){
        val loggingInterceptor =HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }else{
        OkHttpClient.Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory) : Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://www.endlesscreation.kr:8080")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }
}