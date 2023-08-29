package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestSaveCurrentUserInfoUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(userInfo: Any?) =
        memberRepository.saveCurrentUserInfo(userInfo = userInfo)
}