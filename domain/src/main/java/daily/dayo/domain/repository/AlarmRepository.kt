package daily.dayo.domain.repository

import androidx.paging.PagingData
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    suspend fun requestAllAlarmList(): Flow<PagingData<Notification>>
    suspend fun markAlarmAsChecked(alarmId: Int): NetworkResponse<Void>
}