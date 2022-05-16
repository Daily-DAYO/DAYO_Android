package com.daily.dayo.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.login.model.RequestDeviceToken
import com.daily.dayo.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel(){

   fun requestDeviceToken(fcmDeviceToken : String?) = viewModelScope.launch {
        val response = loginRepository.requestDeviceToken(RequestDeviceToken(fcmDeviceToken))
   }
}