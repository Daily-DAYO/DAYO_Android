package com.daily.dayo.setting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.SettingRepository
import com.daily.dayo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val settingRepository: SettingRepository) :
    ViewModel() {

    private val _checkCurrentPasswordSuccess = MutableLiveData<Event<Boolean>>()
    val checkCurrentPasswordSuccess get() = _checkCurrentPasswordSuccess

    private val _changePasswordSuccess = MutableLiveData<Event<Boolean>>()
    val changePasswordSuccess get() = _changePasswordSuccess

    fun requestCheckCurrentPassword(inputPassword: String) = viewModelScope.launch {
        settingRepository.requestCheckCurrentPassword(inputPassword).let {
            if (it.isSuccessful) {
                _checkCurrentPasswordSuccess.postValue(Event(true))
            } else {
                _checkCurrentPasswordSuccess.postValue(Event(false))
            }
        }
    }

    fun requestChangePassword(newPassword: String) = viewModelScope.launch {
        settingRepository.requestChangePassword(newPassword = newPassword).let {
            if (it.isSuccessful) {
                _changePasswordSuccess.postValue(Event(true))
            } else {
                _changePasswordSuccess.postValue(Event(false))
            }
        }
    }
}