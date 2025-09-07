package daily.dayo.domain.usecase.member

import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.WithdrawalReason
import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestResignGuideWordsUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(withdrawalReason: WithdrawalReason): NetworkResponse<List<String>>? =
        when (withdrawalReason) {
            WithdrawalReason.WANT_TO_DELETE_HISTORY -> memberRepository.requestResignGuideRecordWords()
            WithdrawalReason.CONTENT_NOT_SATISFYING -> memberRepository.requestResignGuideFollowWords()
            else -> null // 가이드가 필요 없는 경우 null 반환
        }
}
