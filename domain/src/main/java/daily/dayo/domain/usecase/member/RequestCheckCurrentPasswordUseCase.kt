package daily.dayo.domain.usecase.member

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestCheckCurrentPasswordUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(password: String) =
        memberRepository.requestCheckCurrentPassword(password = password)
}