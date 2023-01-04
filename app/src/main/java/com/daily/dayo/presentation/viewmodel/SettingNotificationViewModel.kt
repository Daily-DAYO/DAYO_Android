package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.common.Status
import com.daily.dayo.data.datasource.remote.member.ChangeReceiveAlarmRequest
import com.daily.dayo.data.datasource.remote.member.DeviceTokenRequest
import com.daily.dayo.domain.usecase.member.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    fun requestDeviceToken(fcmDeviceToken: String?) = viewModelScope.launch {
        val response = requestDeviceTokenUseCase(DeviceTokenRequest(deviceToken = fcmDeviceToken))
    }

    fun requestReceiveAlarm() = viewModelScope.launch {
        requestReceiveAlarmUseCase().let { ApiResponse ->
            when (ApiResponse.status) {
                Status.SUCCESS -> {
                    _notiReactionPermit.postValue(ApiResponse.data?.onReceiveAlarm)
                }
            }
        }
    }

    fun requestReceiveChangeReceiveAlarm(onReceiveAlarm: Boolean) = viewModelScope.launch {
        val response =
            requestChangeReceiveAlarmUseCase(ChangeReceiveAlarmRequest(onReceiveAlarm = onReceiveAlarm))
    }

    fun getCurrentFcmToken() = viewModelScope.launch {
        val currentFcmToken = getCurrentFcmTokenUseCase()
    }

    fun registerFcmToken() = viewModelScope.launch {
        registerFcmTokenUseCase()
    }

    fun unregisterFcmToken() {
        unregisterFcmTokenUseCase()
    }
}