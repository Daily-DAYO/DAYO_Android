package daily.dayo.domain.usecase.block

import daily.dayo.domain.repository.MemberRepository
import javax.inject.Inject

class RequestBlockListUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke() =
        memberRepository.requestBlockList()
}