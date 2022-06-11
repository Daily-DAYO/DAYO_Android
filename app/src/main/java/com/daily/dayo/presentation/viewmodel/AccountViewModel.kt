package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.Event
import com.daily.dayo.data.datasource.remote.member.DeviceTokenRequest
import com.daily.dayo.data.datasource.remote.member.MemberOAuthRequest
import com.daily.dayo.data.datasource.remote.member.MemberSignInRequest
import com.daily.dayo.domain.usecase.member.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val requestLoginKakaoUseCase: RequestLoginKakaoUseCase,
    private val requestLoginEmailUseCase: RequestLoginEmailUseCase,
    private val requestRefreshTokenUseCase: RequestRefreshTokenUseCase,
    private val requestMemberInfoUseCase: RequestMemberInfoUseCase,
    private val requestSignUpEmailUseCase: RequestSignUpEmailUseCase,
    private val requestCheckEmailDuplicateUseCase: RequestCheckEmailDuplicateUseCase,
    private val requestCertificateEmailUseCase: RequestCertificateEmailUseCase,
    private val requestDeviceTokenUseCase: RequestDeviceTokenUseCase,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
    private val requestResignUseCase: RequestResignUseCase
) : ViewModel() {

    private val _signupSuccess = MutableLiveData<Event<Boolean>>()
    val signupSuccess: LiveData<Event<Boolean>> get() = _signupSuccess

    private val _loginSuccess = MutableLiveData<Event<Boolean>>()
    val loginSuccess: LiveData<Event<Boolean>> get() = _loginSuccess

    private val _isEmailDuplicate = MutableLiveData<Boolean>()
    val isEmailDuplicate: LiveData<Boolean> get() = _isEmailDuplicate

    private val _isCertificateEmailSend = MutableLiveData<Boolean>()
    val isCertificateEmailSend: LiveData<Boolean> get() = _isCertificateEmailSend

    private val _certificateEmailAuthCode = MutableLiveData<String>()
    val certificateEmailAuthCode: MutableLiveData<String> get() = _certificateEmailAuthCode

    private val _withdrawSuccess = MutableLiveData<Event<Boolean>>()
    val withdrawSuccess: LiveData<Event<Boolean>> get() = _withdrawSuccess

    fun requestLoginKakao(accessToken: String) = viewModelScope.launch {
        val response = requestLoginKakaoUseCase(MemberOAuthRequest(accessToken = accessToken))
        if (response.isSuccessful) {
            DayoApplication.preferences.saveCurrentUser(response.body())
            requestMemberInfo()
            _loginSuccess.postValue(Event(true))
        } else {
            _loginSuccess.postValue(Event(false))
        }
    }

    fun requestLoginEmail(email: String, password: String) = viewModelScope.launch {
        requestLoginEmailUseCase(MemberSignInRequest(email = email, password = password)).let {
            if (it.isSuccessful) {
                DayoApplication.preferences.saveCurrentUser(it.body())
                requestMemberInfo()
                _loginSuccess.postValue(Event(true))
            } else {
                _loginSuccess.postValue(Event(false))
            }
        }
    }

    fun requestRefreshToken() = viewModelScope.launch {
        requestRefreshTokenUseCase().let {
            if (it.isSuccessful) {
                it.body()?.let { response ->
                    DayoApplication.preferences.setAccessToken(response.accessToken)
                }
                requestMemberInfo()
                _loginSuccess.postValue(Event(true))
            } else
                _loginSuccess.postValue(Event(false))
        }
    }

    private fun requestMemberInfo() = viewModelScope.launch {
        val response = requestMemberInfoUseCase()
        if (response.isSuccessful) {
            DayoApplication.preferences.saveCurrentUser(response.body())
        }
    }

    fun requestSignupEmail(email: String, nickname: String, password: String, profileImg: File?) =
        viewModelScope.launch(Dispatchers.IO) {
            val response = requestSignUpEmailUseCase(email, nickname, password, profileImg)
            if (response.isSuccessful) {
                _signupSuccess.postValue(Event(true))
            } else {
                _signupSuccess.postValue(Event(false))
            }
        }

    fun requestCheckEmailDuplicate(email: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestCheckEmailDuplicateUseCase(email)
        if (response.isSuccessful) {
            _isEmailDuplicate.postValue(true)
        } else {
            _isEmailDuplicate.postValue(false)
        }
    }

    fun requestCertificateEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestCertificateEmailUseCase(email)
        if (response.isSuccessful) {
            _isCertificateEmailSend.postValue(true)
            certificateEmailAuthCode.postValue(response.body()?.authCode)
        } else {
            _isCertificateEmailSend.postValue(false)
        }
    }

    fun requestDeviceToken(deviceToken: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestDeviceTokenUseCase(DeviceTokenRequest(deviceToken = deviceToken))
    }

    fun registerFcmToken() = viewModelScope.launch {
        registerFcmTokenUseCase()
    }

    fun requestWithdraw(content: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestResignUseCase(content)
        if(response.isSuccessful) {
            _withdrawSuccess.postValue(Event(true))
        } else {
            _withdrawSuccess.postValue(Event(false))
        }
    }
}
