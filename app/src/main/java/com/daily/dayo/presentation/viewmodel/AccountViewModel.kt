package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.Event
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.usecase.member.*
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val requestCheckNicknameDuplicateUseCase: RequestCheckNicknameDuplicateUseCase,
    private val requestCertificateEmailUseCase: RequestCertificateEmailUseCase,
    private val requestDeviceTokenUseCase: RequestDeviceTokenUseCase,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
    private val getCurrentFcmTokenUseCase: GetCurrentFcmTokenUseCase,
    private val requestResignUseCase: RequestResignUseCase,
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val requestCheckEmailUseCase: RequestCheckEmailUseCase,
    private val requestCheckEmailAuthUseCase: RequestCheckEmailAuthUseCase,
    private val requestCheckCurrentPasswordUseCase: RequestCheckCurrentPasswordUseCase,
    private val requestChangePasswordUseCase: RequestChangePasswordUseCase,
    private val requestSettingChangePasswordUseCase: RequestSettingChangePasswordUseCase
) : ViewModel() {

    private val _signupSuccess = MutableLiveData<Event<Boolean>>()
    val signupSuccess: LiveData<Event<Boolean>> get() = _signupSuccess

    private val _loginSuccess = MutableLiveData<Event<Boolean>>()
    val loginSuccess: LiveData<Event<Boolean>> get() = _loginSuccess

    private val _memberInfoSuccess = MutableLiveData<Boolean>()
    val memberInfoSuccess get() = _memberInfoSuccess

    private val _isEmailDuplicate = MutableLiveData<Boolean>()
    val isEmailDuplicate: LiveData<Boolean> get() = _isEmailDuplicate

    private val _isNicknameDuplicate = MutableLiveData<Boolean>()
    val isNicknameDuplicate: LiveData<Boolean> get() = _isNicknameDuplicate

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

    private val _isErrorExceptionOccurred = MutableLiveData<Event<Boolean>>()
    val isErrorExceptionOccurred get() = _isErrorExceptionOccurred

    private val _isApiErrorExceptionOccurred = MutableLiveData<Event<Boolean>>()
    val isApiErrorExceptionOccurred get() = _isApiErrorExceptionOccurred

    fun requestLoginKakao(accessToken: String) = viewModelScope.launch {
        requestLoginKakaoUseCase(accessToken = accessToken).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    DayoApplication.preferences.saveCurrentUser(ApiResponse.body)
                    coroutineScope {
                        requestMemberInfo()
                        _loginSuccess.postValue(Event(true))
                    }
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _loginSuccess.postValue(Event(false))
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _loginSuccess.postValue(Event(false))
                }
                is NetworkResponse.UnknownError -> {
                    _loginSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestLoginEmail(email: String, password: String) = viewModelScope.launch {
        requestLoginEmailUseCase(email = email, password = password).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    DayoApplication.preferences.saveCurrentUser(ApiResponse.body)
                    requestMemberInfo()
                    _loginSuccess.postValue(Event(true))
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _loginSuccess.postValue(Event(false))
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _loginSuccess.postValue(Event(false))
                }
                is NetworkResponse.UnknownError -> {
                    _loginSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestRefreshToken() = viewModelScope.launch {
        requestRefreshTokenUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    ApiResponse.body?.let { response ->
                        DayoApplication.preferences.setAccessToken(accessToken = response)
                    }
                    requestMemberInfo()
                    _loginSuccess.postValue(Event(true))
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _loginSuccess.postValue(Event(false))
                }
                is NetworkResponse.ApiError -> {
                    if (ApiResponse.code != 401) _isApiErrorExceptionOccurred.postValue(Event(true))
                    _loginSuccess.postValue(Event(false))
                }
                is NetworkResponse.UnknownError -> {
                    _loginSuccess.postValue(Event(false))
                }
            }
        }
    }

    private suspend fun requestMemberInfo() {
        requestMemberInfoUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    DayoApplication.preferences.saveCurrentUser(ApiResponse.body)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                }
                else -> {}
            }
        }
    }

    fun requestSignupEmail(email: String, nickname: String, password: String, profileImg: File?) =
        viewModelScope.launch {
            requestSignUpEmailUseCase(email, nickname, password, profileImg).let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        _signupSuccess.postValue(Event(true))
                    }
                    is NetworkResponse.NetworkError -> {
                        _isErrorExceptionOccurred.postValue(Event(true))
                        _signupSuccess.postValue(Event(false))
                    }
                    is NetworkResponse.ApiError -> {
                        _isApiErrorExceptionOccurred.postValue(Event(true))
                        _signupSuccess.postValue(Event(false))
                    }
                    else -> {}
                }
            }
        }

    fun requestCheckEmailDuplicate(email: String) = viewModelScope.launch {
        requestCheckEmailDuplicateUseCase(email).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _isEmailDuplicate.postValue(true)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _isEmailDuplicate.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _isEmailDuplicate.postValue(false)
                }
                else -> {}
            }
        }
    }

    fun requestCheckNicknameDuplicate(nickname: String) = viewModelScope.launch {
        requestCheckNicknameDuplicateUseCase(nickname).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _isNicknameDuplicate.postValue(true)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _isNicknameDuplicate.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _isNicknameDuplicate.postValue(false)
                }
                else -> {}
            }
        }
    }

    fun requestCertificateEmail(email: String) = viewModelScope.launch {
        requestCertificateEmailUseCase(email).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _isCertificateEmailSend.postValue(true)
                    certificateEmailAuthCode.postValue(ApiResponse.body)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _isCertificateEmailSend.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _isCertificateEmailSend.postValue(false)
                }
                else -> {}
            }
        }
    }

    suspend fun requestDeviceToken(deviceToken: String) = coroutineScope {
        if (DayoApplication.preferences.notiDevicePermit) registerFcmTokenUseCase()
        requestDeviceTokenUseCase(deviceToken = deviceToken)
    }

    suspend fun getCurrentFcmToken() = getCurrentFcmTokenUseCase()

    fun requestWithdraw(content: String) = viewModelScope.launch {
        requestResignUseCase(content).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _withdrawSuccess.postValue(Event(true))
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _withdrawSuccess.postValue(Event(false))
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _withdrawSuccess.postValue(Event(false))
                }
                else -> {}
            }
        }
    }

    fun requestLogout() = viewModelScope.launch {
        requestLogoutUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _logoutSuccess.postValue(Event(true))
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _logoutSuccess.postValue(Event(false))
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _logoutSuccess.postValue(Event(false))
                }
                else -> {}
            }
        }
    }

    fun requestCheckEmail(inputEmail: String) = viewModelScope.launch {
        requestCheckEmailUseCase(email = inputEmail).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _checkEmailSuccess.postValue(true)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _checkEmailSuccess.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _checkEmailSuccess.postValue(false)
                }
                else -> {}
            }
        }
    }

    fun requestCheckEmailAuth(inputEmail: String) = viewModelScope.launch {
        requestCheckEmailAuthUseCase(inputEmail).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _isCertificateEmailSend.postValue(true)
                    certificateEmailAuthCode.postValue(ApiResponse.body)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _isCertificateEmailSend.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _isCertificateEmailSend.postValue(false)
                }
                else -> {}
            }
        }
    }

    fun requestCheckCurrentPassword(inputPassword: String) = viewModelScope.launch {
        requestCheckCurrentPasswordUseCase(password = inputPassword).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _checkCurrentPasswordSuccess.postValue(true)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _checkCurrentPasswordSuccess.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _checkCurrentPasswordSuccess.postValue(false)
                }
                else -> {}
            }
        }
    }

    fun requestChangePassword(email: String, newPassword: String) = viewModelScope.launch {
        requestChangePasswordUseCase(email = email, password = newPassword).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _changePasswordSuccess.postValue(true)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _changePasswordSuccess.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _changePasswordSuccess.postValue(false)
                }
                else -> {}
            }
        }
    }

    fun requestChangePassword(newPassword: String) = viewModelScope.launch {
        requestSettingChangePasswordUseCase(
            email = DayoApplication.preferences.getCurrentUser().email!!,
            password = newPassword
        ).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _changePasswordSuccess.postValue(true)
                }
                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _changePasswordSuccess.postValue(false)
                }
                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _changePasswordSuccess.postValue(false)
                }
                else -> {}
            }
        }
    }
}
