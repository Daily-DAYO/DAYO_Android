package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.profile.model.ResponseAllFolderList
import com.daily.dayo.profile.model.ResponseCreateFollow
import com.daily.dayo.profile.model.ResponseOtherProfile
import com.daily.dayo.repository.FolderRepository
import com.daily.dayo.repository.FollowRepository
import com.daily.dayo.repository.ProfileRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val profileRepository: ProfileRepository,
    private val followRepository: FollowRepository
) : ViewModel() {

    private val _folderList = MutableLiveData<Resource<ResponseAllFolderList>>()
    val folderList : LiveData<Resource<ResponseAllFolderList>> get() = _folderList

    private val _otherProfile = MutableLiveData<Resource<ResponseOtherProfile>>()
    val otherProfile : LiveData<Resource<ResponseOtherProfile>> get() = _otherProfile

    val followSuccess = MutableLiveData<Boolean>()
    val unfollowSuccess = MutableLiveData<Boolean>()

    fun requestAllFolderList(memberId:String) = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        folderRepository.requestAllFolderList(memberId).let {
            if(it.isSuccessful){
                _folderList.postValue(Resource.success(it.body()))
            } else{
                _folderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestOtherProfile(memberId: String) = viewModelScope.launch {
        profileRepository.requestOtherProfile(memberId).let {
            if(it.isSuccessful){
                _otherProfile.postValue(Resource.success(it.body()))
            }else{
                _otherProfile.postValue(Resource.error(it.errorBody().toString(),null))
            }
        }
    }

    fun requestCreateFollow(followerId:String) = viewModelScope.launch {
        followRepository.requestCreateFollow(followerId).let {
            if(it.isSuccessful){
                followSuccess.postValue(true)
            }
            else{
                followSuccess.postValue(false)
            }
        }
    }

    fun requestDeleteFollow(followerId:String) = viewModelScope.launch {
        followRepository.requestDeleteFollow(followerId).let {
            if(it.isSuccessful){
                unfollowSuccess.postValue(true)
            }
            else{
                unfollowSuccess.postValue(false)
            }
        }
    }


}