package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.usecase.member.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingNotificationViewModel @Inject constructor(
    private val requestDeviceTokenUseCase: RequestDeviceTokenUseCase,
    private val requestReceiveAlarmUseCase: RequestReceiveAlarmUseCase,
    private val requestChangeReceiveAlarmUseCase: RequestChangeReceiveAlarmUseCase,
    private val getCurrentFcmTokenUseCase: GetCurrentFcmTokenUseCase,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
    private val unregisterFcmTokenUseCase: UnregisterFcmTokenUseCase
) : ViewModel() {

    private val _notiReactionPermit = MutableLiveData<Boolean>()
    val notiReactionPermit: LiveData<Boolean> get() = _notiReactionPermit

    fun requestReceiveAlarm() = viewModelScope.launch {
        requestReceiveAlarmUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    ApiResponse.body?.let {
                        _notiReactionPermit.postValue(it)
                    }
                }
                else -> {

                }
            }
        }
    }

    fun requestReceiveChangeReceiveAlarm(onReceiveAlarm: Boolean) = viewModelScope.launch {
        val response =
            requestChangeReceiveAlarmUseCase(onReceiveAlarm = onReceiveAlarm)
    }

    fun registerDeviceToken() = viewModelScope.launch {
        registerFcmTokenUseCase()
        requestDeviceTokenUseCase(deviceToken = getCurrentFcmTokenUseCase())
    }

    fun unregisterFcmToken() = unregisterFcmTokenUseCase()
}