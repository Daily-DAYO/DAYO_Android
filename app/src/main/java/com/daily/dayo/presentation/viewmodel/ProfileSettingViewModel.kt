package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toProfile
import com.daily.dayo.domain.model.Profile
import com.daily.dayo.domain.usecase.member.RequestOtherProfileUseCase
import com.daily.dayo.domain.usecase.member.RequestUpdateMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel@Inject constructor(
    private val requestOtherProfileUseCase: RequestOtherProfileUseCase,
    private val requestUpdateMyProfileUseCase: RequestUpdateMyProfileUseCase
) : ViewModel() {

    private val _profileInfo = MutableLiveData<Resource<Profile>>()
    val profileInfo: LiveData<Resource<Profile>> get() = _profileInfo

    private val _updateSuccess = MutableLiveData<Event<Boolean>>()
    val updateSuccess: LiveData<Event<Boolean>> get() = _updateSuccess

    fun requestProfile(memberId: String) = viewModelScope.launch {
        val response = requestOtherProfileUseCase(memberId = memberId)
        if(response.isSuccessful){
            _profileInfo.postValue(Resource.success(response.body()?.toProfile()))
        }else{
            _profileInfo.postValue(Resource.error(response.errorBody().toString(),null))
        }
    }

    fun requestUpdateMyProfile(nickname: String?, profileImg: File?) = viewModelScope.launch {
        requestUpdateMyProfileUseCase(nickname = nickname, profileImg = profileImg).let {
            if(it.isSuccessful){
                _updateSuccess.postValue(Event(true))
            }
            else{
                _updateSuccess.postValue(Event(false))
            }
        }
    }
}