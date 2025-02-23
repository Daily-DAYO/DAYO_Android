package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestSignInKakaoUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(accessToken: String) =
        memberRepository.requestSignInKakao(accessToken = accessToken)
}