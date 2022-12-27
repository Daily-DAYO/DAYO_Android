package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.block.BlockApiService
import com.daily.dayo.data.datasource.remote.block.BlockRequest
import com.daily.dayo.domain.repository.BlockRepository
import retrofit2.Response
import javax.inject.Inject

class BlockRepositoryImpl @Inject constructor(
    private val blockApiService: BlockApiService
) : BlockRepository {

    override suspend fun requestBlockMember(memberId: String): Response<Void> =
        blockApiService.requestBlockMember(BlockRequest(memberId = memberId))
}