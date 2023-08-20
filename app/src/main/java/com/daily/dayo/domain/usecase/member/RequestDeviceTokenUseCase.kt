package com.daily.dayo.domain.usecase.member

import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestDeviceTokenUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(deviceToken: String?) =
        memberRepository.requestDeviceToken(deviceToken = deviceToken)
}