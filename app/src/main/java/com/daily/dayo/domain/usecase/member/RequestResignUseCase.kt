package com.daily.dayo.domain.usecase.member

import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestResignUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(content: String) =
        memberRepository.requestResign(content)
}