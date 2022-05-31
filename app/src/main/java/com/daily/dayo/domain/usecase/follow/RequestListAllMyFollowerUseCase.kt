package com.daily.dayo.domain.usecase.follow

import com.daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class RequestListAllMyFollowerUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke() =
        followRepository.requestListAllMyFollower()
}