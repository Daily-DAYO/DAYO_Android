package daily.dayo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.usecase.member.*
import daily.dayo.presentation.service.firebase.FirebaseMessagingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingNotificationViewModel @Inject constructor(
    private val requestDeviceTokenUseCase: RequestDeviceTokenUseCase,
    private val requestReceiveAlarmUseCase: RequestReceiveAlarmUseCase,
    private val requestChangeReceiveAlarmUseCase: RequestChangeReceiveAlarmUseCase,
) : ViewModel() {

    private val _isReactionNotificationEnabled = MutableStateFlow<Boolean>(false)
    val isReactionNotificationEnabled: StateFlow<Boolean> get() = _isReactionNotificationEnabled

    fun requestReceiveAlarm() = viewModelScope.launch {
        requestReceiveAlarmUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    ApiResponse.body?.let {
                        _isReactionNotificationEnabled.emit(it)
                    }
                }
                else -> {

                }
            }
        }
    }

    fun requestReceiveChangeReceiveAlarm(onReceiveAlarm: Boolean) = viewModelScope.launch {
        requestChangeReceiveAlarmUseCase(onReceiveAlarm = onReceiveAlarm).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _isReactionNotificationEnabled.emit(onReceiveAlarm)
                }
                else -> {

                }
            }
        }
    }

    fun registerDeviceToken() = viewModelScope.launch {
        FirebaseMessagingService().registerFcmToken()
        requestDeviceTokenUseCase(deviceToken = FirebaseMessagingService().getCurrentToken())
    }

    fun unregisterFcmToken() = FirebaseMessagingService().unregisterFcmToken()
}