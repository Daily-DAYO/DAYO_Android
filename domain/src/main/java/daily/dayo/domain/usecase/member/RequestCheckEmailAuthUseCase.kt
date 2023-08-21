package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestCheckEmailAuthUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(email: String) = memberRepository.requestCheckEmailAuth(email)
}