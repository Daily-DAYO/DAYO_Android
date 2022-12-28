package com.daily.dayo.domain.usecase.block

import com.daily.dayo.domain.repository.BlockRepository
import javax.inject.Inject

class RequestBlockMemberUseCase @Inject constructor(
    private val blockRepository: BlockRepository
) {
    suspend operator fun invoke(memberId: String) =
        blockRepository.requestBlockMember(memberId = memberId)
}