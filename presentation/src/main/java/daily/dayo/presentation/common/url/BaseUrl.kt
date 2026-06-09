package daily.dayo.presentation.common.url

import androidx.compose.runtime.compositionLocalOf
import daily.dayo.presentation.BuildConfig

val LocalBaseUrl = compositionLocalOf { BuildConfig.FALLBACK_BASE_URL }

fun remoteUrl(baseUrl: String, path: String): String {
    val normalizedBaseUrl = baseUrl.trimEnd('/')
    val normalizedPath = path.trimStart('/')

    return when {
        normalizedBaseUrl.isEmpty() -> normalizedPath
        normalizedPath.isEmpty() -> normalizedBaseUrl
        else -> "$normalizedBaseUrl/$normalizedPath"
    }
}

fun remoteImageUrl(baseUrl: String, imageFileName: String?): String =
    remoteUrl(baseUrl = baseUrl, path = "images/$imageFileName")
