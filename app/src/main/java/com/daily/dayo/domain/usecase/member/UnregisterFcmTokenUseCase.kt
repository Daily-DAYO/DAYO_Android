package com.daily.dayo.domain.usecase.member

import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class UnregisterFcmTokenUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() =
        memberRepository.unregisterFcmToken()
}