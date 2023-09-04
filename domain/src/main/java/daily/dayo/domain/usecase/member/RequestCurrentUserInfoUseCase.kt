package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestCurrentUserInfoUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() =
        memberRepository.requestCurrentUserInfo()
}