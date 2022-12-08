package com.daily.dayo.data.di

import com.daily.dayo.BuildConfig
import com.daily.dayo.DayoApplication
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
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                // Header에 AccessToken을 삽입하지 않는 대상
                if (request.url.encodedPath.equals("/api/v1/members/kakaoOAuth", true) ||
                    request.url.encodedPath.equals("/api/v1/members/signIn", true) ||
                    request.url.encodedPath.startsWith("/api/v1/members/signUp", true)
                ) {
                    chain.proceed(request)
                } else if (request.url.encodedPath.equals("/api/v1/members/refresh", true)) {
                    chain.proceed(request.newBuilder().apply {
                        addHeader(
                            "Authorization",
                            "Bearer ${DayoApplication.preferences.getCurrentUser().refreshToken.toString()}"
                        )
                    }.build())
                } else {
                    chain.proceed(request.newBuilder().apply {
                        addHeader(
                            "Authorization",
                            "Bearer ${DayoApplication.preferences.getCurrentUser().accessToken.toString()}"
                        )
                    }.build())
                }
            }
            .addInterceptor(loggingInterceptor)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://117.17.198.45:8080")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }
}