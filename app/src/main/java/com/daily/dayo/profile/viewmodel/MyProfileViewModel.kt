package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.profile.model.ResponseAllMyFolderList
import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.repository.FolderRepository
import com.daily.dayo.repository.ProfileRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel@Inject constructor(
    private val folderRepository: FolderRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _folderList = MutableLiveData<Resource<ResponseAllMyFolderList>>()
    val folderList : LiveData<Resource<ResponseAllMyFolderList>> get() = _folderList

    private val _myProfile = MutableLiveData<Resource<ResponseMyProfile>>()
    val myProfile : LiveData<Resource<ResponseMyProfile>> get() = _myProfile

    init {
        requestAllMyFolderList()
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        folderRepository.requestAllMyFolderList().let {
            if(it.isSuccessful){
                _folderList.postValue(Resource.success(it.body()))
            } else{
                _folderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestMyProfile() = viewModelScope.launch {
        profileRepository.requestMyProfile().let {
            if(it.isSuccessful){
                _myProfile.postValue(Resource.success(it.body()))
            }else{
                _myProfile.postValue(Resource.error(it.errorBody().toString(),null))
            }
        }
    }

    suspend fun requestUpdateMyProfile(nickname: String?, profileImg: File?) = viewModelScope.launch {
        profileRepository.requestUpdateMyProfile(nickname, profileImg)
    }
}