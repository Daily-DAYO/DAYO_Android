package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Notification
import daily.dayo.domain.usecase.notification.RequestAllAlarmListUseCase
import daily.dayo.domain.usecase.notification.RequestIsCheckAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val requestAllAlarmListUseCase: RequestAllAlarmListUseCase,
    private val requestIsCheckAlarmUseCase: RequestIsCheckAlarmUseCase
) : ViewModel() {

    private val _alarmList = MutableLiveData<PagingData<Notification>>()
    val alarmList: LiveData<PagingData<Notification>> get() = _alarmList

    private val _checkAlarmSuccess = MutableLiveData<Boolean>()
    val checkAlarmSuccess: LiveData<Boolean> get() = _checkAlarmSuccess

    fun requestAllAlarmList() = viewModelScope.launch {
        requestAllAlarmListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _alarmList.postValue(it) }
    }

    fun requestIsCheckAlarm(alarmId: Int) = viewModelScope.launch {
        requestIsCheckAlarmUseCase(alarmId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> _checkAlarmSuccess.postValue(true)
                else -> _checkAlarmSuccess.postValue(false)
            }
        }
    }
}