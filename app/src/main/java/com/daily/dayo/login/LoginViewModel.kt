package com.daily.dayo.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel(){

    fun loginRequest(request: LoginRequest) =
        viewModelScope.launch (Dispatchers.IO){
            loginRepository.requestLogin(request)
        }
}
