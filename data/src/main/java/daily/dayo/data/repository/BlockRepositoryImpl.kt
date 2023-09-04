package daily.dayo.data.repository

import daily.dayo.data.datasource.remote.block.BlockApiService
import daily.dayo.data.datasource.remote.block.BlockRequest
import daily.dayo.data.datasource.remote.block.UnblockRequest
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.BlockRepository
import javax.inject.Inject

class BlockRepositoryImpl @Inject constructor(
    private val blockApiService: BlockApiService
) : BlockRepository {

    override suspend fun requestBlockMember(memberId: String): NetworkResponse<Void> =
        blockApiService.requestBlockMember(BlockRequest(memberId = memberId))

    override suspend fun requestUnblockMember(memberId: String): NetworkResponse<Void> =
        blockApiService.requestUnblockMember(UnblockRequest(memberId = memberId))
}