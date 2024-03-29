package daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import daily.dayo.data.datasource.remote.alarm.AlarmApiService
import daily.dayo.data.datasource.remote.alarm.AlarmPagingSource
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.AlarmRepository
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmApiService: AlarmApiService
) : AlarmRepository {

    override suspend fun requestAllAlarmList() =
        Pager(PagingConfig(pageSize = ALARM_PAGE_SIZE)) {
            AlarmPagingSource(alarmApiService, ALARM_PAGE_SIZE)
        }.flow

    override suspend fun requestIsCheckAlarm(alarmId: Int): NetworkResponse<Void> =
        alarmApiService.requestIsCheckAlarm(alarmId)

    companion object {
        private const val ALARM_PAGE_SIZE = 10
    }
}