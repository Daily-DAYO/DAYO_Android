package com.daily.dayo.domain.repository

import retrofit2.Response

interface ImageRepository {

    suspend fun requestDownloadImage(filename: String): Response<Void>
}