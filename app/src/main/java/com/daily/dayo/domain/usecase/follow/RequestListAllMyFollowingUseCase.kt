package com.daily.dayo.domain.usecase.follow

import com.daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class RequestListAllMyFollowingUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke() =
        followRepository.requestListAllMyFollowing()
}