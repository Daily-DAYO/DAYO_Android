package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestClearCurrentUserUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() =
        memberRepository.clearClearCurrentUser()
}