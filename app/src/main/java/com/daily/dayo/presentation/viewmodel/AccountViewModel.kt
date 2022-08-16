package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.Event
import com.daily.dayo.data.datasource.remote.member.ChangePasswordRequest
import com.daily.dayo.data.datasource.remote.member.CheckPasswordRequest
import com.daily.dayo.data.datasource.remote.member.DeviceTokenRequest
import com.daily.dayo.data.datasource.remote.member.MemberOAuthRequest
import com.daily.dayo.data.datasource.remote.member.MemberSignInRequest
import com.daily.dayo.domain.usecase.member.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
    private val requestResignUseCase: RequestResignUseCase,
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val requestCheckEmailUseCase: RequestCheckEmailUseCase,
    private val requestCheckCurrentPasswordUseCase: RequestCheckCurrentPasswordUseCase,
    private val requestChangePasswordUseCase: RequestChangePasswordUseCase,
    private val requestSettingChangePasswordUseCase: RequestSettingChangePasswordUseCase,
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

    private val _logoutSuccess = MutableLiveData<Event<Boolean>>()
    val logoutSuccess: LiveData<Event<Boolean>> get() = _logoutSuccess

    private val _checkEmailSuccess = MutableLiveData<Boolean>()
    val checkEmailSuccess get() = _checkEmailSuccess

    private val _checkCurrentPasswordSuccess = MutableLiveData<Boolean>()
    val checkCurrentPasswordSuccess get() = _checkCurrentPasswordSuccess

    private val _changePasswordSuccess = MutableLiveData<Boolean>()
    val changePasswordSuccess get() = _changePasswordSuccess

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

    suspend fun requestDeviceToken(deviceToken: String) = coroutineScope {
        requestDeviceTokenUseCase(DeviceTokenRequest(deviceToken = deviceToken))
    }

    suspend fun registerFcmToken() = coroutineScope {
        registerFcmTokenUseCase()
    }

    fun requestWithdraw(content: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = requestResignUseCase(content)
        if (response.isSuccessful) {
            _withdrawSuccess.postValue(Event(true))
        } else {
            _withdrawSuccess.postValue(Event(false))
        }
    }

    fun requestLogout() = viewModelScope.launch {
        requestLogoutUseCase().let {
            if (it.isSuccessful) {
                _logoutSuccess.postValue(Event(true))
            } else {
                _logoutSuccess.postValue(Event(false))
            }
        }
    }

    fun requestCheckEmail(inputEmail: String) = viewModelScope.launch {
        requestCheckEmailUseCase(email = inputEmail).let {
            if (it.isSuccessful) {
                _checkEmailSuccess.postValue(true)
            } else {
                _checkEmailSuccess.postValue(false)
            }
        }
    }

    fun requestCheckCurrentPassword(inputPassword: String) = viewModelScope.launch {
        requestCheckCurrentPasswordUseCase(CheckPasswordRequest(password = inputPassword)).let {
            if (it.isSuccessful) {
                _checkCurrentPasswordSuccess.postValue(true)
            } else {
                _checkCurrentPasswordSuccess.postValue(false)
            }
        }
    }

    fun requestChangePassword(email: String, newPassword: String) = viewModelScope.launch {
        requestChangePasswordUseCase(
            ChangePasswordRequest(
                email = email,
                password = newPassword
            )
        ).let {
            if (it.isSuccessful) {
                _changePasswordSuccess.postValue(true)
            } else {
                _changePasswordSuccess.postValue(false)
            }
        }
    }

    fun requestChangePassword(newPassword: String) = viewModelScope.launch {
        requestSettingChangePasswordUseCase(
            ChangePasswordRequest(
                email = DayoApplication.preferences.getCurrentUser().email!!,
                password = newPassword
            )
        ).let {
            if (it.isSuccessful) {
                _changePasswordSuccess.postValue(true)
            } else {
                _changePasswordSuccess.postValue(false)
            }
        }
    }
}
