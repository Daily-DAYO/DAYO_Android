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
import daily.dayo.domain.usecase.notification.MarkAlarmAsCheckedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val requestAllAlarmListUseCase: RequestAllAlarmListUseCase,
    private val markAlarmAsCheckedUseCase: MarkAlarmAsCheckedUseCase
) : ViewModel() {

    private val _alarmList = MutableStateFlow<PagingData<Notification>>(PagingData.empty())
    val alarmList: StateFlow<PagingData<Notification>> get() = _alarmList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _checkAlarmSuccess = MutableLiveData<Boolean>()
    val checkAlarmSuccess: LiveData<Boolean> get() = _checkAlarmSuccess

    fun loadNotifications() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            requestAllAlarmList()
            _isRefreshing.emit(false)
        }
    }

    fun requestAllAlarmList() = viewModelScope.launch {
        requestAllAlarmListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _alarmList.emit(it) }
    }

    fun markAlarmAsChecked(alarmId: Int) = viewModelScope.launch {
        markAlarmAsCheckedUseCase(alarmId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> _checkAlarmSuccess.postValue(true)
                else -> _checkAlarmSuccess.postValue(false)
            }
        }
    }
}