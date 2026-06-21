package daily.dayo.domain.provider

enum class BaseUrlSource {
    FALLBACK,
    CACHED_LAST_SUCCESS,
    REMOTE_CONFIG
}

interface BaseUrlProvider {
    fun getBaseUrl(): String

    fun getBaseUrlSource(): BaseUrlSource

    fun isTrustedBaseUrl(baseUrl: String): Boolean

    suspend fun refreshBaseUrl(): Boolean
}
