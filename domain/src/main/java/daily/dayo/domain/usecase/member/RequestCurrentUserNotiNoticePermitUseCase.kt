package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestCurrentUserNotiNoticePermitUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() =
        memberRepository.requestCurrentUserNotiNoticePermit()
    operator fun invoke(isPermit: Boolean) =
        memberRepository.requestCurrentUserNotiNoticePermit(isPermit = isPermit)
}