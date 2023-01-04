package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.common.Status
import com.daily.dayo.data.mapper.toProfile
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

    private val _profileInfo = MutableLiveData<Resource<Profile>>()
    val profileInfo: LiveData<Resource<Profile>> get() = _profileInfo

    private val _updateSuccess = MutableLiveData<Event<Boolean>>()
    val updateSuccess: LiveData<Event<Boolean>> get() = _updateSuccess

    fun requestProfile(memberId: String) = viewModelScope.launch {
        requestOtherProfileUseCase(memberId = memberId).let { ApiResponse ->
            when (ApiResponse.status) {
                Status.SUCCESS -> {
                    _profileInfo.postValue(Resource.success(ApiResponse.data?.toProfile()))
                }
                Status.ERROR -> {
                    _profileInfo.postValue(Resource.error(ApiResponse.exception))
                }
                Status.API_ERROR -> {
                    _profileInfo.postValue(Resource(Status.API_ERROR, ApiResponse.data?.toProfile(), ApiResponse.message, null))
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
                when (ApiResponse.status) {
                    Status.SUCCESS -> {
                        _updateSuccess.postValue(Event(true))
                    }
                    Status.ERROR -> {
                        _updateSuccess.postValue(Event(false))
                    }
                    Status.API_ERROR -> {
                        _updateSuccess.postValue(Event(false))
                    }
                }
            }
        }
}