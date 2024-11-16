package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Profile
import daily.dayo.domain.model.UserBlocked
import daily.dayo.domain.usecase.block.RequestBlockListUseCase
import daily.dayo.domain.usecase.member.RequestCheckNicknameDuplicateUseCase
import daily.dayo.domain.usecase.member.RequestMyProfileUseCase
import daily.dayo.domain.usecase.member.RequestUpdateMyProfileUseCase
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    private val requestMyProfileUseCase: RequestMyProfileUseCase,
    private val requestUpdateMyProfileUseCase: RequestUpdateMyProfileUseCase,
    private val requestBlockListUseCase: RequestBlockListUseCase,
    private val requestCheckNicknameDuplicateUseCase: RequestCheckNicknameDuplicateUseCase
) : ViewModel() {

    private val _profileInfo = MutableLiveData<Resource<Profile>>()
    val profileInfo: LiveData<Resource<Profile>> = _profileInfo

    private val _updateSuccess = MutableLiveData<Event<Boolean>>()
    val updateSuccess: LiveData<Event<Boolean>> get() = _updateSuccess

    private val _blockList = MutableLiveData<Resource<List<UserBlocked>>>()
    val blockList: LiveData<Resource<List<UserBlocked>>> get() = _blockList

    private val _isNicknameDuplicate = MutableSharedFlow<Boolean>()
    val isNicknameDuplicate = _isNicknameDuplicate.asSharedFlow()

    private val _isErrorExceptionOccurred = MutableLiveData<Event<Boolean>>()
    val isErrorExceptionOccurred get() = _isErrorExceptionOccurred

    private val _isApiErrorExceptionOccurred = MutableLiveData<Event<Boolean>>()
    val isApiErrorExceptionOccurred get() = _isApiErrorExceptionOccurred

    init {
        requestMyProfile()
    }

    fun requestUpdateMyProfile(nickname: String?, profileImg: File?, isReset: Boolean) =
        viewModelScope.launch {
            requestUpdateMyProfileUseCase(
                nickname = nickname,
                profileImg = profileImg,
                onBasicProfileImg = isReset
            ).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _updateSuccess.postValue(Event(true))
                    }

                    is NetworkResponse.NetworkError -> {
                        _updateSuccess.postValue(Event(false))
                    }

                    is NetworkResponse.ApiError -> {
                        _updateSuccess.postValue(Event(false))
                    }

                    is NetworkResponse.UnknownError -> {
                        _updateSuccess.postValue(Event(false))
                    }
                }
            }
        }

    fun requestBlockList() = viewModelScope.launch {
        requestBlockListUseCase().let { response ->
            when (response) {
                is NetworkResponse.Success -> {
                    _blockList.postValue(Resource.success(response.body?.data))
                }

                is NetworkResponse.NetworkError -> {
                    _blockList.postValue(Resource.error(response.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _blockList.postValue(Resource.error(response.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _blockList.postValue(Resource.error(response.throwable.toString(), null))
                }
            }
        }
    }

    fun requestCheckNicknameDuplicate(nickname: String) {
        if (nickname == profileInfo.value?.data?.nickname) return
        viewModelScope.launch {
            requestCheckNicknameDuplicateUseCase(nickname).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isNicknameDuplicate.emit(false)
                    }

                    is NetworkResponse.ApiError -> {
                        _isNicknameDuplicate.emit(true)
                    }

                    else -> {
                        _isErrorExceptionOccurred.postValue(Event(true))
                    }
                }
            }
        }
    }

    private fun requestMyProfile() {
        viewModelScope.launch {
            requestMyProfileUseCase().let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _profileInfo.postValue(Resource.success(response.body))
                    }

                    else -> {
                        _isErrorExceptionOccurred.postValue(Event(true))
                    }
                }
            }
        }
    }
}
