package com.daily.dayo.domain.usecase.image

import com.daily.dayo.domain.repository.ImageRepository
import javax.inject.Inject

class RequestDownloadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(filename: String) =
        imageRepository.requestDownloadImage(filename)
}