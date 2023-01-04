package com.daily.dayo.common

import io.sentry.Sentry
import io.sentry.SentryLevel
import retrofit2.Response
import java.net.URLDecoder

data class Resource<out T>(
    val status: Status, val data: T?, val message: String?, val exception: Exception?
) {
    // create some utility files to retrieve data inside other package.
    // These classes will be used to wrap our data to be used in a generic way into our UI.
    // 사용할 데이터를 Wrapping하여 Success, error, loading 판단
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg, null)
        }

        fun <T> error(code: String, msg: String): Resource<T> {
            val resource = Resource(
                Status.API_ERROR,
                null,
                "${URLDecoder.decode(code, "UTF-8")}:${URLDecoder.decode(msg, "UTF-8")}",
                null
            )
            // TODO 응답 코드에 따라서 Sentry CaptureMessage 예외처리 필요
            Sentry.captureMessage("[API ERROR] $resource",SentryLevel.ERROR)
            return resource
        }

        fun <T> error(exception: Exception?): Resource<T> =
            Resource(Status.ERROR, null, null, exception)

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null, null)
        }
    }
}

fun <T> Response<T>.toResource(): Resource<T> {
    return if (this.isSuccessful) {
        Resource.success(this.body())
    } else {
        val code = this.code()
        val message = this.message() ?: ""
        Resource.error(
            URLDecoder.decode(code.toString(), "UTF-8"),
            URLDecoder.decode(message, "UTF-8")
        )
    }
}

suspend fun <T> setExceptionHandling(requestApi: suspend () -> Response<T>): Resource<T> =
    try {
        requestApi.invoke().toResource()
    } catch (e: Exception) {
        Resource.error(e)
    }