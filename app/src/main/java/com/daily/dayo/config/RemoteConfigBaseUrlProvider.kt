package com.daily.dayo.config

import android.content.Context
import android.content.SharedPreferences
import com.daily.dayo.BuildConfig
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import daily.dayo.domain.provider.BaseUrlSource
import daily.dayo.domain.provider.BaseUrlProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.net.InetAddress
import java.net.URI
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RemoteConfigBaseUrlProvider(
    context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) : BaseUrlProvider {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    @Volatile
    private var currentBaseUrlState: BaseUrlState = getInitialBaseUrlState()

    init {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(FETCH_TIMEOUT_SECONDS)
                .setMinimumFetchIntervalInSeconds(MINIMUM_FETCH_INTERVAL_SECONDS)
                .build()
        )
        remoteConfig.setDefaultsAsync(
            mapOf(
                BuildConfig.REMOTE_CONFIG_BASE_URL_KEY to BuildConfig.FALLBACK_BASE_URL,
                BuildConfig.REMOTE_CONFIG_BASE_URL_SIGNATURE_KEY to ""
            )
        )
    }

    override fun getBaseUrl(): String = currentBaseUrlState.baseUrl

    override fun getBaseUrlSource(): BaseUrlSource = currentBaseUrlState.source

    override fun isTrustedBaseUrl(baseUrl: String): Boolean {
        val normalizedBaseUrl = normalizeBaseUrl(baseUrl) ?: return false
        return normalizedBaseUrl == currentBaseUrlState.baseUrl ||
            normalizedBaseUrl == normalizeBaseUrl(BuildConfig.FALLBACK_BASE_URL)
    }

    override suspend fun refreshBaseUrl(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().awaitWithTimeout() ?: return false
            val remoteBaseUrlValue = remoteConfig.getValue(BuildConfig.REMOTE_CONFIG_BASE_URL_KEY)
            if (remoteBaseUrlValue.source != FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                return false
            }

            val remoteBaseUrl = remoteBaseUrlValue.asString()
            val normalizedBaseUrl = normalizeBaseUrl(remoteBaseUrl) ?: run {
                return false
            }
            if (!isTrustedRemoteBaseUrl(normalizedBaseUrl)) {
                return false
            }

            currentBaseUrlState = BaseUrlState(
                baseUrl = normalizedBaseUrl,
                source = BaseUrlSource.REMOTE_CONFIG
            )
            sharedPreferences.edit()
                .putString(KEY_LAST_SUCCESSFUL_BASE_URL, normalizedBaseUrl)
                .apply()
            true
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            false
        }
    }

    private fun getInitialBaseUrlState(): BaseUrlState {
        val cachedBaseUrl = sharedPreferences.getString(KEY_LAST_SUCCESSFUL_BASE_URL, null)
        normalizeBaseUrl(cachedBaseUrl)?.let { baseUrl ->
            return BaseUrlState(
                baseUrl = baseUrl,
                source = BaseUrlSource.CACHED_LAST_SUCCESS
            )
        }

        return BaseUrlState(
            baseUrl = normalizeBaseUrl(BuildConfig.FALLBACK_BASE_URL) ?: BuildConfig.FALLBACK_BASE_URL,
            source = BaseUrlSource.FALLBACK
        )
    }

    private suspend fun Task<Boolean>.awaitWithTimeout(): Boolean? {
        return withTimeoutOrNull(TASK_TIMEOUT_MILLIS) {
            suspendCancellableCoroutine { continuation ->
                addOnCompleteListener { task ->
                    if (!continuation.isActive) return@addOnCompleteListener

                    val exception = task.exception
                    if (exception != null) {
                        continuation.resumeWithException(exception)
                    } else {
                        continuation.resume(task.result == true)
                    }
                }
            }
        }
    }

    private fun normalizeBaseUrl(baseUrl: String?): String? {
        val trimmedBaseUrl = baseUrl?.trim().orEmpty()
        if (trimmedBaseUrl.isEmpty()) return null

        return try {
            val uri = URI(trimmedBaseUrl)
            if (uri.rawQuery != null || uri.rawFragment != null) return null

            val scheme = uri.scheme?.lowercase(Locale.US) ?: return null
            val host = uri.host?.takeIf { it.isNotBlank() } ?: return null
            if (scheme != SCHEME_HTTP && scheme != SCHEME_HTTPS) return null
            if (!BuildConfig.DEBUG && scheme == SCHEME_HTTP && !isReleaseHttpAllowed()) return null
            if (uri.userInfo != null) return null
            if (!BuildConfig.DEBUG && isUnsafeReleaseHost(host)) return null

            val path = uri.path.orEmpty()
            if (path.isNotBlank() && path != "/") return null

            val normalizedUri = URI(scheme, null, host, uri.port, "/", null, null)
            normalizedUri.toString().ensureTrailingSlash()
        } catch (exception: Exception) {
            null
        }
    }

    private fun isTrustedRemoteBaseUrl(normalizedBaseUrl: String): Boolean {
        if (normalizedBaseUrl == normalizeBaseUrl(BuildConfig.FALLBACK_BASE_URL)) return true
        if (BuildConfig.DEBUG) return true

        return verifyBaseUrlSignature(
            baseUrl = normalizedBaseUrl,
            signature = remoteConfig.getString(BuildConfig.REMOTE_CONFIG_BASE_URL_SIGNATURE_KEY)
        )
    }

    private fun verifyBaseUrlSignature(baseUrl: String, signature: String): Boolean {
        val publicKeyText = BuildConfig.REMOTE_CONFIG_BASE_URL_PUBLIC_KEY.stripPemFormatting()
        val signatureText = signature.stripPemFormatting()
        if (publicKeyText.isBlank() || signatureText.isBlank()) return false

        return runCatching {
            val publicKey = KeyFactory.getInstance(KEY_ALGORITHM_RSA).generatePublic(
                X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText))
            )
            val verifier = Signature.getInstance(SIGNATURE_ALGORITHM)
            verifier.initVerify(publicKey)
            verifier.update(baseUrl.toByteArray(Charsets.UTF_8))
            verifier.verify(Base64.getDecoder().decode(signatureText))
        }.getOrDefault(false)
    }

    private fun isReleaseHttpAllowed(): Boolean =
        runCatching {
            URI(BuildConfig.FALLBACK_BASE_URL).scheme?.lowercase(Locale.US) == SCHEME_HTTP
        }.getOrDefault(false)

    private fun isUnsafeReleaseHost(host: String): Boolean {
        val normalizedHost = host.trim('[', ']')
        if (normalizedHost.equals("localhost", ignoreCase = true)) return true
        if (normalizedHost.endsWith(".localhost", ignoreCase = true)) return true
        if (normalizedHost.endsWith(".local", ignoreCase = true)) return true
        if (!normalizedHost.looksLikeIpLiteral()) return false

        return runCatching {
            val address = InetAddress.getByName(normalizedHost)
            address.isAnyLocalAddress ||
                address.isLoopbackAddress ||
                address.isLinkLocalAddress ||
                address.isSiteLocalAddress ||
                address.isMulticastAddress
        }.getOrDefault(true)
    }

    private fun String.looksLikeIpLiteral(): Boolean =
        IPV4_ADDRESS_REGEX.matches(this) || contains(':')

    private fun String.stripPemFormatting(): String =
        replace(PEM_PUBLIC_KEY_BEGIN, "")
            .replace(PEM_PUBLIC_KEY_END, "")
            .replace(PEM_SIGNATURE_BEGIN, "")
            .replace(PEM_SIGNATURE_END, "")
            .filterNot { it.isWhitespace() }

    private fun String.ensureTrailingSlash(): String =
        if (endsWith('/')) this else "$this/"

    private data class BaseUrlState(
        val baseUrl: String,
        val source: BaseUrlSource
    )

    companion object {
        private const val PREFERENCES_NAME = "remote_config_base_url"
        private const val KEY_LAST_SUCCESSFUL_BASE_URL = "last_successful_base_url"
        private const val FETCH_TIMEOUT_SECONDS = 5L
        private const val TASK_TIMEOUT_MILLIS = 6_000L
        private val MINIMUM_FETCH_INTERVAL_SECONDS = if (BuildConfig.DEBUG) 0L else 3_600L
        private const val SCHEME_HTTP = "http"
        private const val SCHEME_HTTPS = "https"
        private const val KEY_ALGORITHM_RSA = "RSA"
        private const val SIGNATURE_ALGORITHM = "SHA256withRSA"
        private const val PEM_PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----"
        private const val PEM_PUBLIC_KEY_END = "-----END PUBLIC KEY-----"
        private const val PEM_SIGNATURE_BEGIN = "-----BEGIN SIGNATURE-----"
        private const val PEM_SIGNATURE_END = "-----END SIGNATURE-----"
        private val IPV4_ADDRESS_REGEX = Regex("""\d{1,3}(\.\d{1,3}){3}""")
    }
}
