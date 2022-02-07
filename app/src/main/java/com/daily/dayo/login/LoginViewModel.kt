package com.daily.dayo.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.login.model.SignupEmailRequest
import com.daily.dayo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel(){

    private val _signupSuccess = MutableLiveData<Event<Boolean>>()
    val signupSuccess : LiveData<Event<Boolean>> get() =_signupSuccess

    fun requestLogin(request: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.requestLogin(request)
        }
    }

    fun requestMemberInfo() = viewModelScope.launch{
        loginRepository.requestMemberInfo()
    }

    fun requestSignupEmail(email: String, nickname: String, password: String, profileImg: File?) = viewModelScope.launch(Dispatchers.IO) {
        val response = loginRepository.requestSignupEmail(email, nickname, password, profileImg)
        if (response.isSuccessful) {
            _signupSuccess.postValue(Event(true))
        } else {
            _signupSuccess.postValue(Event(false))
        }
    }
}
