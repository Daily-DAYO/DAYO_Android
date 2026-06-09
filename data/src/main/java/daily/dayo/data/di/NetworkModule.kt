package daily.dayo.data.di

import android.content.Context
import daily.dayo.data.BuildConfig
import daily.dayo.data.datasource.remote.retrofit.factory.NetworkResponseAdapterFactory
import daily.dayo.data.datasource.remote.retrofit.interceptor.BaseUrlRewriteInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import daily.dayo.data.datasource.local.SharedManager
import daily.dayo.domain.provider.BaseUrlProvider
import okhttp3.HttpUrl
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
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        baseUrlRewriteInterceptor: BaseUrlRewriteInterceptor,
        baseUrlProvider: BaseUrlProvider
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.redactHeader("Authorization")
        loggingInterceptor.setLevel(
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        )
        return OkHttpClient.Builder()
            .addInterceptor(baseUrlRewriteInterceptor)
            .addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                val shouldAttachAuthorization = baseUrlProvider.isTrustedBaseUrl(request.url.toBaseOrigin())
                // Header에 AccessToken을 삽입하지 않는 대상
                if (request.url.encodedPath.equals("/api/v1/members/kakaoOAuth", true) ||
                    request.url.encodedPath.equals("/api/v1/members/signIn", true) ||
                    request.url.encodedPath.startsWith("/api/v1/members/signUp", true)
                ) {
                    chain.proceed(request)
                } else if (!shouldAttachAuthorization) {
                    chain.proceed(request)
                } else if (request.url.encodedPath.equals("/api/v1/members/refresh", true)) {
                    chain.proceed(request.newBuilder().apply {
                        addHeader(
                            "Authorization",
                            "Bearer ${SharedManager(context = context).getCurrentUser().refreshToken.toString()}"
                        )
                    }.build())
                } else {
                    chain.proceed(request.newBuilder().apply {
                        addHeader(
                            "Authorization",
                            "Bearer ${SharedManager(context = context).getCurrentUser().accessToken.toString()}"
                        )
                    }.build())
                }
            }
            .addInterceptor(loggingInterceptor)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    private fun HttpUrl.toBaseOrigin(): String =
        newBuilder()
            .encodedPath("/")
            .query(null)
            .build()
            .toString()

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
            .baseUrl(BuildConfig.FALLBACK_BASE_URL)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }
}
