package com.daily.dayo.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.SharedManager
import com.daily.dayo.login.model.LoginRequestEmail
import com.daily.dayo.login.model.LoginRequestKakao
import com.daily.dayo.login.model.RequestDeviceToken
import com.daily.dayo.repository.LoginRepository
import com.daily.dayo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository, private val sharedManager: SharedManager) : ViewModel(){

    private val _signupSuccess = MutableLiveData<Event<Boolean>>()
    val signupSuccess : LiveData<Event<Boolean>> get() =_signupSuccess

    private val _loginSuccess = MutableLiveData<Event<Boolean>>()
    val loginSuccess : LiveData<Event<Boolean>> get() =_loginSuccess

    private val _isEmailDuplicate = MutableLiveData<Boolean>()
    val isEmailDuplicate : LiveData<Boolean> get() = _isEmailDuplicate

    private val _isCertificateEmailSend = MutableLiveData<Boolean>()
    val isCertificateEmailSend : LiveData<Boolean> get() = _isCertificateEmailSend
    private val _certificateEmailAuthCode = MutableLiveData<String>()
    val certificateEmailAuthCode : MutableLiveData<String> get() = _certificateEmailAuthCode

    fun requestLoginKakao(request: LoginRequestKakao) = viewModelScope.launch {
        val response = loginRepository.requestLoginKakao(request)
        if (response.isSuccessful) {
            sharedManager.saveCurrentUser(response.body())
            requestMemberInfo()
            _loginSuccess.postValue(Event(true))
        } else {
            _loginSuccess.postValue(Event(false))
        }
    }

    fun requestLoginEmail(request: LoginRequestEmail) = viewModelScope.launch {
        val response = loginRepository.requestLoginEmail(request)
        if (response.isSuccessful) {
            sharedManager.saveCurrentUser(response.body())
            requestMemberInfo()
            _loginSuccess.postValue(Event(true))
        } else {
            _loginSuccess.postValue(Event(false))
        }
    }

    fun requestRefreshToken() = viewModelScope.launch {
        val response = loginRepository.requestRefreshToken()
        if(response.isSuccessful){
            response.body()?.let { sharedManager.setAccessToken(it.accessToken) }
            requestMemberInfo()
            _loginSuccess.postValue(Event(true))
        } else
            _loginSuccess.postValue(Event(false))
    }

    private fun requestMemberInfo() = viewModelScope.launch{
        val response = loginRepository.requestMemberInfo()
        if (response.isSuccessful) {
            sharedManager.saveCurrentUser(response.body())
        }
    }

    fun requestSignupEmail(email: String, nickname: String, password: String, profileImg: File?) = viewModelScope.launch(Dispatchers.IO) {
        val response = loginRepository.requestSignupEmail(email, nickname, password, profileImg)
        if (response.isSuccessful) {
            _signupSuccess.postValue(Event(true))
        } else {
            _signupSuccess.postValue(Event(false))
        }
    }

    fun requestCheckEmailDuplicate(email: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = loginRepository.requestCheckEmailDuplicate(email)
        if (response.isSuccessful) {
            _isEmailDuplicate.postValue(true)
        } else {
            _isEmailDuplicate.postValue(false)
        }
    }

    fun requestCertificateEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = loginRepository.requestCertificateEmail(email)
        if (response.isSuccessful) {
            _isCertificateEmailSend.postValue(true)
            certificateEmailAuthCode.postValue(response.body()?.authCode)
        } else {
            _isCertificateEmailSend.postValue(false)
        }
    }

    suspend fun requestDeviceToken(fcmDeviceToken : String) = coroutineScope {
        val response = loginRepository.requestDeviceToken(RequestDeviceToken(fcmDeviceToken))
    }
}
