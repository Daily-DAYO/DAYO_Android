package com.daily.dayo.domain.repository

import retrofit2.Response

interface BlockRepository {
    suspend fun requestBlockMember(memberId: String): Response<Void>
}