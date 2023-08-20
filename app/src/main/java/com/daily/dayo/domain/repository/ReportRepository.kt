package com.daily.dayo.domain.repository

import com.daily.dayo.domain.model.NetworkResponse

interface ReportRepository {

    suspend fun requestSaveMemberReport(comment: String, memberId: String): NetworkResponse<Void>
    suspend fun requestSavePostReport(comment: String, postId: Int): NetworkResponse<Void>
}