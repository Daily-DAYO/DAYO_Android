package com.daily.dayo.domain.usecase.block

import com.daily.dayo.domain.repository.BlockRepository
import javax.inject.Inject

class RequestUnblockMemberUseCase @Inject constructor(
    private val blockRepository: BlockRepository
) {
    suspend operator fun invoke(memberId: String) =
        blockRepository.requestUnblockMember(memberId = memberId)
}