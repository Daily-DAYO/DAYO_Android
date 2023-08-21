package daily.dayo.domain.repository

import androidx.paging.PagingData
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Notice
import daily.dayo.domain.model.NoticeDetail
import kotlinx.coroutines.flow.Flow

interface NoticeRepository {
    suspend fun requestAllNoticeList(): Flow<PagingData<Notice>>
    suspend fun requestDetailNotice(noticeId: Int): NetworkResponse<NoticeDetail>
}