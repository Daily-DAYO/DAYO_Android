package daily.dayo.data.datasource.remote.retrofit.interceptor

import daily.dayo.domain.provider.BaseUrlProvider
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlRewriteInterceptor @Inject constructor(
    private val baseUrlProvider: BaseUrlProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val providerBaseUrl = baseUrlProvider.getBaseUrl()
        if (!baseUrlProvider.isTrustedBaseUrl(providerBaseUrl)) return chain.proceed(request)

        val providerUrl = providerBaseUrl.toHttpUrlOrNull()
            ?: return chain.proceed(request)

        return try {
            val rewrittenUrl = request.url.newBuilder()
                .scheme(providerUrl.scheme)
                .host(providerUrl.host)
                .port(providerUrl.port)
                .build()
            chain.proceed(
                request.newBuilder()
                    .url(rewrittenUrl)
                    .build()
            )
        } catch (exception: IllegalArgumentException) {
            chain.proceed(request)
        }
    }
}
