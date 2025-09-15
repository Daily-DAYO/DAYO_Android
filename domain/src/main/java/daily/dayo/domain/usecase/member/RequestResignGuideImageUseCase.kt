package daily.dayo.domain.usecase.member

import daily.dayo.domain.model.WithdrawalReason
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestResignGuideImageUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(fileName: String, withdrawalReason: WithdrawalReason): NetworkResponse<ByteArray>? =
        when (withdrawalReason) {
            WithdrawalReason.WANT_TO_DELETE_HISTORY -> memberRepository.requestResignGuideRecordImage(fileName)
            WithdrawalReason.CONTENT_NOT_SATISFYING -> memberRepository.requestResignGuideFollowImage(fileName)
            else -> null
        }
}