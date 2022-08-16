package com.daily.dayo.domain.usecase.member

import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestCheckEmailUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(email: String) = memberRepository.requestCheckEmail(email)
}