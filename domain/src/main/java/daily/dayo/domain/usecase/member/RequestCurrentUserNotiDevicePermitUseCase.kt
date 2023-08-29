package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestCurrentUserNotiDevicePermitUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() =
        memberRepository.requestCurrentUserNotiDevicePermit()
    operator fun invoke(isPermit: Boolean) =
        memberRepository.requestCurrentUserNotiDevicePermit(isPermit = isPermit)
}