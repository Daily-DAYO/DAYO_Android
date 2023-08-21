package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestChangePasswordUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(email: String, password: String) =
        memberRepository.requestChangePassword(email = email, password = password)
}