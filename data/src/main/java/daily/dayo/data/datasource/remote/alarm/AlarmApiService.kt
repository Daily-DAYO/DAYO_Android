package daily.dayo.data.datasource.remote.alarm

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AlarmApiService {

    @GET("/api/v1/alarms")
    suspend fun requestAllAlarmList(@Query("end") end: Int): NetworkResponse<ListAllAlarmResponse>

    @POST("/api/v1/alarms/{alarmId}")
    suspend fun markAlarmAsChecked(@Path("alarmId") alarmId: Int): NetworkResponse<Void>
}