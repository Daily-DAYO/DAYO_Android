package com.daily.dayo.domain.usecase.follow

import com.daily.dayo.data.datasource.remote.follow.CreateFollowUpRequest
import com.daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class RequestCreateFollowUpUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(body: CreateFollowUpRequest) =
        followRepository.requestCreateFollowUp(body)
}