package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.image.ImageApiService
import com.daily.dayo.domain.repository.ImageRepository
import retrofit2.Response
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageApiService: ImageApiService
) : ImageRepository {

    override suspend fun requestDownloadImage(filename: String): Response<Void> =
        imageApiService.requestDownloadImage(filename)
}