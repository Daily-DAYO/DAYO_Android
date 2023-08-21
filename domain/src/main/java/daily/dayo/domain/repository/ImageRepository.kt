package daily.dayo.domain.repository

import daily.dayo.domain.model.NetworkResponse

interface ImageRepository {

    suspend fun requestDownloadImage(filename: String): NetworkResponse<Void>
}