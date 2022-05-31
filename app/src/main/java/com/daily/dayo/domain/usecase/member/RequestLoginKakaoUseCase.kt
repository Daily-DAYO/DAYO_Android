package com.daily.dayo.domain.usecase.member

import com.daily.dayo.data.datasource.remote.member.MemberOAuthRequest
import com.daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestLoginKakaoUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(body: MemberOAuthRequest) =
        memberRepository.requestLoginKakao(body)
}