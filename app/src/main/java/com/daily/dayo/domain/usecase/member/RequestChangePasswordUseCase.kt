package com.daily.dayo.domain.usecase.member

import com.daily.dayo.data.datasource.remote.member.ChangePasswordRequest
import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestChangePasswordUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(body: ChangePasswordRequest) = memberRepository.requestChangePassword(body)
}