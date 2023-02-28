package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toBlockUser
import com.daily.dayo.data.mapper.toProfile
import com.daily.dayo.domain.model.BlockUser
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Profile
import com.daily.dayo.domain.usecase.block.RequestBlockListUseCase
import com.daily.dayo.domain.usecase.member.RequestOtherProfileUseCase
import com.daily.dayo.domain.usecase.member.RequestUpdateMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    private val requestOtherProfileUseCase: RequestOtherProfileUseCase,
    private val requestUpdateMyProfileUseCase: RequestUpdateMyProfileUseCase,
    private val requestBlockListUseCase: RequestBlockListUseCase
) : ViewModel() {

    private val _profileInfo = MutableLiveData<Profile>()
    val profileInfo: LiveData<Profile> get() = _profileInfo

    private val _updateSuccess = MutableLiveData<Event<Boolean>>()
    val updateSuccess: LiveData<Event<Boolean>> get() = _updateSuccess

    private val _blockList = MutableLiveData<Resource<List<BlockUser>>>()
    val blockList: LiveData<Resource<List<BlockUser>>> get() = _blockList

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
                    is NetworkResponse.ApiError -> {
                        _updateSuccess.postValue(Event(false))
                    }
                }
            }
        }

    fun requestBlockList() = viewModelScope.launch {
        requestBlockListUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _blockList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toBlockUser() }))
                }
                is NetworkResponse.NetworkError -> {
                    _blockList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }
                is NetworkResponse.ApiError -> {
                    _blockList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }
                is NetworkResponse.UnknownError -> {
                    _blockList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

}