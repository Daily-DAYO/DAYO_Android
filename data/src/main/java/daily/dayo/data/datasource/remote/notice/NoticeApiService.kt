package daily.dayo.data.datasource.remote.notice

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeApiService {

    @GET("/api/v1/notice")
    suspend fun requestAllNoticeList(@Query("end") end: Int): NetworkResponse<NoticeListResponse>

    @GET("/api/v1/notice/{noticeId}")
    suspend fun requestDetailNotice(@Path("noticeId") noticeId: Int): NetworkResponse<NoticeDetailResponse>
}