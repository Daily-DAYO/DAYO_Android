package daily.dayo.domain.repository

import daily.dayo.domain.model.NetworkResponse


interface BlockRepository {
    suspend fun requestBlockMember(memberId: String): NetworkResponse<Void>
    suspend fun requestUnblockMember(memberId: String): NetworkResponse<Void>
}