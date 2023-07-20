package com.daily.dayo.domain.repository

import com.daily.dayo.domain.model.NetworkResponse

interface ImageRepository {

    suspend fun requestDownloadImage(filename: String): NetworkResponse<Void>
}