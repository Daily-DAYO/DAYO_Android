package daily.dayo.data.repository

import daily.dayo.data.datasource.remote.image.ImageApiService
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageApiService: ImageApiService
) : ImageRepository {

    override suspend fun requestDownloadImage(filename: String): NetworkResponse<Void> =
        imageApiService.requestDownloadImage(filename)
}