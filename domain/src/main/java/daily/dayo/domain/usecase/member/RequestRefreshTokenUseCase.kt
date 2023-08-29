package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRefreshTokenUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke() =
        memberRepository.requestRefreshToken()
}