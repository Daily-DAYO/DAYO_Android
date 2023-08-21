package daily.dayo.domain.usecase.image

import daily.dayo.domain.repository.ImageRepository
import javax.inject.Inject

class RequestDownloadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(filename: String) =
        imageRepository.requestDownloadImage(filename)
}