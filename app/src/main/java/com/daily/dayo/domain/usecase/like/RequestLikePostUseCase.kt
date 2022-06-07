package com.daily.dayo.domain.usecase.like

import com.daily.dayo.data.datasource.remote.heart.CreateHeartRequest
import com.daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class RequestLikePostUseCase @Inject constructor(
    private val heartRepository: HeartRepository
) {
    suspend operator fun invoke(body: CreateHeartRequest) =
        heartRepository.requestLikePost(body)
}