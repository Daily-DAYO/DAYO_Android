package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toNotification
import com.daily.dayo.domain.model.Notification
import com.daily.dayo.domain.usecase.notification.RequestAllAlarmListUseCase
import com.daily.dayo.domain.usecase.notification.RequestIsCheckAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val requestAllAlarmListUseCase: RequestAllAlarmListUseCase,
    private val requestIsCheckAlarmUseCase: RequestIsCheckAlarmUseCase
) : ViewModel() {

    private val _alarmList = MutableLiveData<Resource<List<Notification>>>()
    val alarmList: LiveData<Resource<List<Notification>>> get() = _alarmList

    private val _checkAlarmSuccess = MutableLiveData<Boolean>()
    val checkAlarmSuccess: LiveData<Boolean> get() = _checkAlarmSuccess

    fun requestAllAlarmList() = viewModelScope.launch {
        _alarmList.postValue(Resource.loading(null))
        val response = requestAllAlarmListUseCase()
        if (response.isSuccessful) {
            _alarmList.postValue(Resource.success(response.body()?.data?.map { it.toNotification() }))
        } else {
            _alarmList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestIsCheckAlarm(alarmId: Int) = viewModelScope.launch {
        if (requestIsCheckAlarmUseCase(alarmId).isSuccessful)
            _checkAlarmSuccess.postValue(true)
        else
            _checkAlarmSuccess.postValue(false)
    }
}