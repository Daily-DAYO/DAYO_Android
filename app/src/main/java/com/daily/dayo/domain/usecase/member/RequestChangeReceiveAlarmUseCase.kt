package com.daily.dayo.domain.usecase.member

import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestChangeReceiveAlarmUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(onReceiveAlarm: Boolean) =
        memberRepository.requestChangeReceiveAlarm(onReceiveAlarm = onReceiveAlarm)
}