package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.data.mapper.toProfile
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Profile
import com.daily.dayo.domain.usecase.member.RequestOtherProfileUseCase
import com.daily.dayo.domain.usecase.member.RequestUpdateMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    private val requestOtherProfileUseCase: RequestOtherProfileUseCase,
    private val requestUpdateMyProfileUseCase: RequestUpdateMyProfileUseCase
) : ViewModel() {

    private val _profileInfo = MutableLiveData<Profile>()
    val profileInfo: LiveData<Profile> get() = _profileInfo

    private val _updateSuccess = MutableLiveData<Event<Boolean>>()
    val updateSuccess: LiveData<Event<Boolean>> get() = _updateSuccess

    fun requestProfile(memberId: String) = viewModelScope.launch {
        requestOtherProfileUseCase(memberId = memberId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _profileInfo.postValue(ApiResponse.body?.toProfile())
                }
                is NetworkResponse.NetworkError -> {

                }
                is NetworkResponse.ApiError -> {

                }
            }
        }
    }

    fun requestUpdateMyProfile(nickname: String?, profileImg: File?, isReset: Boolean) =
        viewModelScope.launch {
            requestUpdateMyProfileUseCase(
                nickname = nickname,
                profileImg = profileImg,
                onBasicProfileImg = isReset
            ).let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        _updateSuccess.postValue(Event(true))
                    }
                    is NetworkResponse.NetworkError -> {
                        _updateSuccess.postValue(Event(false))
                    }
                    is NetworkResponse.ApiError  -> {
                        _updateSuccess.postValue(Event(false))
                    }
                }
            }
        }
}