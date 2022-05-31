package com.daily.dayo.domain.usecase.follow

import com.daily.dayo.data.datasource.remote.follow.CreateFollowRequest
import com.daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class RequestCreateFollowUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(body: CreateFollowRequest) =
        followRepository.requestCreateFollow(body)
}