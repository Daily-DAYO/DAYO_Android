package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestCheckEmailDuplicateUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(email: String) =
        memberRepository.requestCheckEmailDuplicate(email)
}