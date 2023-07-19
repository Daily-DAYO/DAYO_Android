package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.notice.NoticeApiService
import com.daily.dayo.data.datasource.remote.notice.NoticeDetailResponse
import com.daily.dayo.data.datasource.remote.notice.NoticePagingSource
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.NoticeRepository
import javax.inject.Inject

class NoticeRepositoryImpl @Inject constructor(
    private val noticeApiService: NoticeApiService
) : NoticeRepository {

    override suspend fun requestAllNoticeList() =
        Pager(PagingConfig(pageSize = NOTICE_PAGE_SIZE)) {
            NoticePagingSource(noticeApiService, NOTICE_PAGE_SIZE)
        }.flow

    override suspend fun requestDetailNotice(noticeId: Int): NetworkResponse<NoticeDetailResponse> =
        noticeApiService.requestDetailNotice(noticeId)

    companion object {
        private const val NOTICE_PAGE_SIZE = 10
    }
}