package daily.dayo.domain.usecase.notification

import daily.dayo.domain.repository.AlarmRepository
import javax.inject.Inject

class RequestIsCheckAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Int) =
        alarmRepository.requestIsCheckAlarm(alarmId)
}