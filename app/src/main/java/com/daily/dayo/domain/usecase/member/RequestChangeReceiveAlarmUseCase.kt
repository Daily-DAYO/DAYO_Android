package com.daily.dayo.domain.usecase.member

import com.daily.dayo.data.datasource.remote.member.ChangeReceiveAlarmRequest
import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestChangeReceiveAlarm @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(body: ChangeReceiveAlarmRequest) =
        memberRepository.requestChangeReceiveAlarm(body)
}