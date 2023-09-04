package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestSaveCurrentUserAccessTokenUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(accessToken: String) =
        memberRepository.saveCurrentUserAccessToken(accessToken = accessToken)
}