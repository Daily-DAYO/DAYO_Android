package com.daily.dayo.domain.usecase.like

import com.daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class RequestAllMyLikePostListUseCase @Inject constructor(
    private val heartRepository: HeartRepository
) {
    suspend operator fun invoke() =
        heartRepository.requestAllMyLikePostList()
}