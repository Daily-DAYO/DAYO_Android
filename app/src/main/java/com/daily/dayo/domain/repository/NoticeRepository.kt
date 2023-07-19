package com.daily.dayo.domain.repository

import androidx.paging.PagingData
import com.daily.dayo.data.datasource.remote.notice.NoticeDetailResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Notice
import kotlinx.coroutines.flow.Flow

interface NoticeRepository {
    suspend fun requestAllNoticeList(): Flow<PagingData<Notice>>
    suspend fun requestDetailNotice(noticeId: Int): NetworkResponse<NoticeDetailResponse>
}