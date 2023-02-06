package com.daily.dayo.domain.model

import java.io.IOException

sealed class NetworkResponse<out T> {
    data class Success<T: Any>(val body: T?): NetworkResponse<T>()

    data class ApiError(val code: Int, val error: String?) : NetworkResponse<Nothing>()

    data class NetworkError(val exception: IOException) : NetworkResponse<Nothing>()

    data class UnknownError(val throwable: Throwable?) : NetworkResponse<Nothing>()
}
