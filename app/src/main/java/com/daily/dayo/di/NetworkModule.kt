package com.daily.dayo.di

import com.daily.dayo.BuildConfig
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
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
        val sharedManager = SharedManager(DayoApplication.applicationContext())
        val accessToken = sharedManager.getCurrentUser().accessToken.toString()

        val loggingInterceptor =HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor{ chain: Interceptor.Chain ->
                val request = chain.request()
                // Header에 AccessToken을 삽입하지 않는 대상
                if (request.url.encodedPath.equals("/api/v1/members/kakaoOAuth", true)
                ) {
                    chain.proceed(request)
                } else {
                    chain.proceed(request.newBuilder().apply {
                        addHeader("Authorization", "Bearer $accessToken")
                    }.build())
                }
            }
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