package com.daily.dayo.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel(){

    fun requestLogin(request: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.requestLogin(request)
        }
    }

    fun requestMemberInfo() = viewModelScope.launch{
        loginRepository.requestMemberInfo()
    }

}
