package daily.dayo.data.datasource.remote.report

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApiService {

    @POST("/api/v1/reports/member")
    suspend fun requestSaveMemberReport(@Body body: CreateReportMemberRequest): NetworkResponse<Void>

    @POST("/api/v1/reports/post")
    suspend fun requestSavePostReport(@Body body: CreateReportPostRequest): NetworkResponse<Void>
}