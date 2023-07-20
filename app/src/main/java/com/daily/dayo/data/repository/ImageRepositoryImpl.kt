package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.image.ImageApiService
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageApiService: ImageApiService
) : ImageRepository {

    override suspend fun requestDownloadImage(filename: String): NetworkResponse<Void> =
        imageApiService.requestDownloadImage(filename)
}