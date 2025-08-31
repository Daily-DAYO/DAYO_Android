package daily.dayo.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import daily.dayo.presentation.common.Event
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.usecase.member.*
import daily.dayo.presentation.service.firebase.FirebaseMessagingService
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.image.ImageResizeUtil.cropCenterBitmap
import daily.dayo.presentation.common.toFile
import daily.dayo.presentation.screen.account.model.CheckOAuthEmailStatus
import daily.dayo.presentation.screen.account.model.EmailExistenceStatus
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val requestSignInKakaoUseCase: RequestSignInKakaoUseCase,
    private val requestSignInEmailUseCase: RequestSignInEmailUseCase,
    private val requestRefreshTokenUseCase: RequestRefreshTokenUseCase,
    private val requestMemberInfoUseCase: RequestMemberInfoUseCase,
    private val requestSignUpEmailUseCase: RequestSignUpEmailUseCase,
    private val requestCheckEmailDuplicateUseCase: RequestCheckEmailDuplicateUseCase,
    private val requestCheckNicknameDuplicateUseCase: RequestCheckNicknameDuplicateUseCase,
    private val requestCertificateEmailUseCase: RequestCertificateEmailUseCase,
    private val requestDeviceTokenUseCase: RequestDeviceTokenUseCase,
    private val requestResignUseCase: RequestResignUseCase,
    private val requestSignOutUseCase: RequestSignOutUseCase,
    private val requestCheckEmailUseCase: RequestCheckEmailUseCase,
    private val requestCheckOAuthEmailUseCase: RequestCheckOAuthEmailUseCase,
    private val requestCertificateEmailPasswordResetUseCase: RequestCertificateEmailPasswordResetUseCase,
    private val requestCheckCurrentPasswordUseCase: RequestCheckCurrentPasswordUseCase,
    private val requestChangePasswordUseCase: RequestChangePasswordUseCase,
    private val requestSettingChangePasswordUseCase: RequestSettingChangePasswordUseCase,
    private val requestCurrentUserInfoUseCase: RequestCurrentUserInfoUseCase,
    private val requestSaveCurrentUserInfoUseCase: RequestSaveCurrentUserInfoUseCase,
    private val requestSaveCurrentUserAccessTokenUseCase: RequestSaveCurrentUserAccessTokenUseCase,
    private val requestCurrentUserNotiDevicePermitUseCase: RequestCurrentUserNotiDevicePermitUseCase,
    private val requestCurrentUserNotiNoticePermitUseCase: RequestCurrentUserNotiNoticePermitUseCase,
    private val requestClearCurrentUserUseCase: RequestClearCurrentUserUseCase
) : ViewModel() {
    companion object {
        const val EMAIL_CERTIFICATE_AUTH_CODE_INITIAL = Int.MIN_VALUE + 10
        const val SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL = Int.MIN_VALUE + 20
        const val RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_CODE_FAIL = Int.MIN_VALUE + 30
    }

    private val _signupSuccess = MutableStateFlow<Status?>(null)
    val signupSuccess: StateFlow<Status?> get() = _signupSuccess

    private val _signInSuccess: MutableStateFlow<Status?> = MutableStateFlow(null)
    val signInSuccess: StateFlow<Status?> get() = _signInSuccess

    private val _autoSignInSuccess = MutableLiveData<Event<Boolean>>()
    val autoSignInSuccess: LiveData<Event<Boolean>> get() = _autoSignInSuccess

    private val _isEmailDuplicate = MutableStateFlow<Status>(Status.LOADING)
    val isEmailDuplicate: StateFlow<Status> get() = _isEmailDuplicate

    private val _isNicknameDuplicate = MutableStateFlow<Boolean>(false)
    val isNicknameDuplicate: StateFlow<Boolean> get() = _isNicknameDuplicate

    private val _certificateEmailAuthCode =
        MutableStateFlow<String?>(EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString())
    val certificateEmailAuthCode: StateFlow<String?> get() = _certificateEmailAuthCode

    private val _withdrawSuccess = MutableStateFlow<Status?>(null)
    val withdrawSuccess: StateFlow<Status?> get() = _withdrawSuccess

    private val _signOutSuccess = MutableStateFlow<Status?>(null)
    val signOutSuccess: StateFlow<Status?> get() = _signOutSuccess

    private val _checkEmailSuccess = MutableStateFlow<EmailExistenceStatus>(EmailExistenceStatus.IDLE)
    val checkEmailSuccess: StateFlow<EmailExistenceStatus> get() = _checkEmailSuccess

    private val _checkOAuthEmailSuccess = MutableStateFlow<CheckOAuthEmailStatus>(CheckOAuthEmailStatus.LOADING)
    val checkOAuthEmailSuccess: StateFlow<CheckOAuthEmailStatus> get() = _checkOAuthEmailSuccess

    private val _resetPasswordSuccess = MutableStateFlow<Status?>(null)
    val resetPasswordSuccess: StateFlow<Status?> get() = _resetPasswordSuccess

    private val _checkCurrentPasswordSuccess = MutableStateFlow<Boolean?>(null)
    val checkCurrentPasswordSuccess:StateFlow<Boolean?> get() = _checkCurrentPasswordSuccess

    private val _changePasswordSuccess = MutableStateFlow<Status?>(null)
    val changePasswordSuccess: StateFlow<Status?> get() = _changePasswordSuccess

    private val _isErrorExceptionOccurred = MutableLiveData<Event<Boolean>>()
    val isErrorExceptionOccurred get() = _isErrorExceptionOccurred

    private val _isApiErrorExceptionOccurred = MutableLiveData<Event<Boolean>>()
    val isApiErrorExceptionOccurred get() = _isApiErrorExceptionOccurred

    private val _isLoginFailByUncorrected = MutableLiveData<Event<Boolean>>()
    val isLoginFailByUncorrected get() = _isLoginFailByUncorrected

    private val _isNoticeNotificationEnabled = MutableStateFlow<Boolean>(requestCurrentUserNotiNoticePermitUseCase())
    val isNoticeNotificationEnabled: StateFlow<Boolean> get() = _isNoticeNotificationEnabled

    fun initializeSignInSuccess() {
        _signInSuccess.value = null
    }

    fun requestSignInKakao(accessToken: String) = viewModelScope.launch {
        _signInSuccess.emit(Status.LOADING)
        requestSignInKakaoUseCase(accessToken = accessToken).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    requestSaveCurrentUserInfoUseCase(ApiResponse.body)
                    coroutineScope {
                        requestMemberInfo()
                        _signInSuccess.emit(Status.SUCCESS)
                    }
                }

                is NetworkResponse.ApiError -> {
                    _signInSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.NetworkError -> {
                    _signInSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.UnknownError -> {
                    _signInSuccess.emit(Status.ERROR)
                }
            }
        }
    }

    fun requestSignInEmail(email: String, password: String) = viewModelScope.launch {
        _signInSuccess.emit(Status.LOADING)
        requestSignInEmailUseCase(email = email, password = password).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    requestSaveCurrentUserInfoUseCase(ApiResponse.body)
                    requestMemberInfo()
                    _signInSuccess.emit(Status.SUCCESS)
                }

                is NetworkResponse.NetworkError -> {
                    _signInSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    // TODO 404 에러코드 별도 처리
                    _signInSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.UnknownError -> {
                    _signInSuccess.emit(Status.ERROR)
                }
            }
        }
    }

    fun requestRefreshToken() = viewModelScope.launch {
        requestRefreshTokenUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    ApiResponse.body?.let { response ->
                        requestSaveCurrentUserAccessTokenUseCase(accessToken = response)
                    }
                    requestMemberInfo()
                    _autoSignInSuccess.postValue(Event(true))
                }

                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _autoSignInSuccess.postValue(Event(false))
                }

                is NetworkResponse.ApiError -> {
                    if (ApiResponse.code != 401) _isApiErrorExceptionOccurred.postValue(Event(true))
                    _autoSignInSuccess.postValue(Event(false))
                }

                is NetworkResponse.UnknownError -> {
                    _autoSignInSuccess.postValue(Event(false))
                }
            }
        }
    }

    private suspend fun requestMemberInfo() {
        requestMemberInfoUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    requestSaveCurrentUserInfoUseCase(ApiResponse.body)
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

    fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: Bitmap?= null,
        profileImgTempDir: String? = null
    ) =
        viewModelScope.launch {
            _signupSuccess.emit(Status.LOADING)
            val resizedImage = profileImg?.let { selectedImage ->
                if (profileImgTempDir != null) {
                    return@let selectedImage.cropCenterBitmap().toFile(profileImgTempDir)
                } else {
                    return@let null
                }
            }

            requestSignUpEmailUseCase(email, nickname, password, resizedImage).let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        _signupSuccess.emit(Status.SUCCESS)
                    }

                    is NetworkResponse.NetworkError -> {
                        _isErrorExceptionOccurred.postValue(Event(true))
                        _signupSuccess.emit(Status.ERROR)
                    }

                    is NetworkResponse.ApiError -> {
                        _isApiErrorExceptionOccurred.postValue(Event(true))
                        _signupSuccess.emit(Status.ERROR)
                    }

                    else -> {}
                }
            }
        }

    fun requestCheckEmailDuplicate(email: String) = viewModelScope.launch {
        _isEmailDuplicate.emit(Status.LOADING)
        requestCheckEmailDuplicateUseCase(email).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _isEmailDuplicate.emit(Status.SUCCESS)
                }

                is NetworkResponse.NetworkError -> {
                    // TODO 에러 처리 필요
                }

                is NetworkResponse.ApiError -> {
                    // TODO 에러 처리 필요
                    if (ApiResponse.code == 409) {
                        _isEmailDuplicate.emit(Status.ERROR)
                    }
                }

                else -> {}
            }
        }
    }

    fun requestCheckNicknameDuplicate(nickname: String) = viewModelScope.launch {
        requestCheckNicknameDuplicateUseCase(nickname).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _isNicknameDuplicate.emit(false)
                }

                is NetworkResponse.NetworkError -> {
                    // TODO 에러 처리 필요
                }

                is NetworkResponse.ApiError -> {
                    // TODO 에러 처리 필요
                    if (ApiResponse.code == 409) {
                        _isNicknameDuplicate.emit(true)
                    }
                }

                else -> {}
            }
        }
    }

    fun requestCertificateEmail(email: String) = viewModelScope.launch {
        _certificateEmailAuthCode.emit(EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString())
        requestCertificateEmailUseCase(email).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _certificateEmailAuthCode.emit(ApiResponse.body)
                }

                is NetworkResponse.NetworkError -> {
                    _certificateEmailAuthCode.emit(SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString())
                }

                is NetworkResponse.ApiError -> {
                    _certificateEmailAuthCode.emit(SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString())
                }

                else -> {}
            }
        }
    }

    suspend fun requestDeviceToken(deviceToken: String) = coroutineScope {
        if (requestCurrentUserNotiDevicePermitUseCase()) FirebaseMessagingService().registerFcmToken()
        requestDeviceTokenUseCase(deviceToken = deviceToken)
    }

    suspend fun getCurrentFcmToken() = FirebaseMessagingService().getCurrentToken()

    fun requestWithdraw(content: String) = viewModelScope.launch {
        _withdrawSuccess.emit(Status.LOADING)
        requestResignUseCase(content).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _withdrawSuccess.emit(Status.SUCCESS)
                }

                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _withdrawSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _withdrawSuccess.emit(Status.ERROR)
                }

                else -> {}
            }
        }
    }

    fun requestSignOut() = viewModelScope.launch {
        _signOutSuccess.emit(Status.LOADING)
        requestSignOutUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _signOutSuccess.emit(Status.SUCCESS)
                }

                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _signOutSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _signOutSuccess.emit(Status.ERROR)
                }

                else -> {}
            }
        }
    }

    fun requestCheckEmail(inputEmail: String) = viewModelScope.launch {
        _checkEmailSuccess.emit(EmailExistenceStatus.LOADING)
        requestCheckEmailUseCase(email = inputEmail).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _checkEmailSuccess.emit(EmailExistenceStatus.EXISTS)
                }

                is NetworkResponse.NetworkError -> {
                    _checkEmailSuccess.emit(EmailExistenceStatus.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    if (ApiResponse.code == 404) {
                        _checkEmailSuccess.emit(EmailExistenceStatus.NOT_EXISTS)
                    } else {
                        _checkEmailSuccess.emit(EmailExistenceStatus.ERROR)
                    }
                }
                is NetworkResponse.UnknownError -> {
                    _checkEmailSuccess.emit(EmailExistenceStatus.ERROR)
                }
            }
        }
    }

    fun requestCheckOAuthEmail(inputEmail: String) = viewModelScope.launch {
        _checkOAuthEmailSuccess.emit(CheckOAuthEmailStatus.LOADING)
        requestCheckOAuthEmailUseCase(email = inputEmail).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _checkOAuthEmailSuccess.emit(CheckOAuthEmailStatus.NORMAL_EMAIL)
                }

                is NetworkResponse.NetworkError -> {
                    _checkOAuthEmailSuccess.emit(CheckOAuthEmailStatus.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    if (ApiResponse.code == 400) {
                        _checkOAuthEmailSuccess.emit(CheckOAuthEmailStatus.OAUTH_ACCOUNT)
                    } else {
                        _checkOAuthEmailSuccess.emit(CheckOAuthEmailStatus.ERROR)
                    }
                }
                is NetworkResponse.UnknownError -> {
                    _checkOAuthEmailSuccess.emit(CheckOAuthEmailStatus.ERROR)
                }
            }
        }
    }

    fun requestCertificateEmailPasswordReset(inputEmail: String) = viewModelScope.launch {
        _certificateEmailAuthCode.emit(EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString())
        requestCertificateEmailPasswordResetUseCase(inputEmail).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _certificateEmailAuthCode.emit(ApiResponse.body)
                }

                is NetworkResponse.NetworkError -> {
                    _certificateEmailAuthCode.emit(RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString())
                }

                is NetworkResponse.ApiError -> {
                    _certificateEmailAuthCode.emit(RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString())
                }

                else -> {}
            }
        }
    }

    fun requestCheckCurrentPassword(inputPassword: String) = viewModelScope.launch {
        _checkCurrentPasswordSuccess.emit(null)
        requestCheckCurrentPasswordUseCase(password = inputPassword).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _checkCurrentPasswordSuccess.emit(true)
                }

                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _checkCurrentPasswordSuccess.emit(false)
                }

                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _checkCurrentPasswordSuccess.emit(false)
                }

                else -> {}
            }
        }
    }

    fun requestChangePassword(email: String, newPassword: String) = viewModelScope.launch {
        _resetPasswordSuccess.emit(Status.LOADING)
        requestChangePasswordUseCase(email = email, password = newPassword).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _resetPasswordSuccess.emit(Status.SUCCESS)
                }

                is NetworkResponse.NetworkError -> {
                    _resetPasswordSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    _resetPasswordSuccess.emit(Status.ERROR)
                }

                else -> {}
            }
        }
    }

    fun requestChangePassword(newPassword: String) = viewModelScope.launch {
        _changePasswordSuccess.emit(Status.LOADING)
        requestSettingChangePasswordUseCase(
            email = requestCurrentUserInfoUseCase().email!!,
            password = newPassword
        ).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _changePasswordSuccess.emit(Status.SUCCESS)
                }

                is NetworkResponse.NetworkError -> {
                    _isErrorExceptionOccurred.postValue(Event(true))
                    _changePasswordSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    _isApiErrorExceptionOccurred.postValue(Event(true))
                    _changePasswordSuccess.emit(Status.ERROR)
                }

                else -> {}
            }
        }
    }

    fun clearCurrentUser() = requestClearCurrentUserUseCase()
    fun getCurrentUserInfo() = requestCurrentUserInfoUseCase()

    fun getCurrentUserNotiDevicePermit() = requestCurrentUserNotiDevicePermitUseCase()
    fun requestCurrentUserNotiDevicePermit(isPermit: Boolean) =
        requestCurrentUserNotiDevicePermitUseCase(isPermit = isPermit)

    fun changeNoticeNotificationSetting(isPermit: Boolean) = viewModelScope.launch {
        requestCurrentUserNotiNoticePermitUseCase(isPermit = isPermit)
        _isNoticeNotificationEnabled.emit(isPermit)
    }
}
