package com.daily.dayo.follow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.profile.model.ResponseListAllFollow
import com.daily.dayo.repository.FollowRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(private val followRepository: FollowRepository) : ViewModel() {

    private val _memberId = MutableLiveData<String>()
    val memberId: LiveData<String> get() = _memberId

    private val _followerList = MutableLiveData<Resource<ResponseListAllFollow>>()
    val followerList : LiveData<Resource<ResponseListAllFollow>> get() = _followerList

    private val _followingList = MutableLiveData<Resource<ResponseListAllFollow>>()
    val followingList : LiveData<Resource<ResponseListAllFollow>> get() = _followingList

    val followSuccess = MutableLiveData<Boolean>()
    val unfollowSuccess = MutableLiveData<Boolean>()

    fun setMemberId(id: String) {
        _memberId.value = id
    }

    fun requestListAllFollower(memberId: String) = viewModelScope.launch {
        followRepository.requestListAllFollower(memberId).let {
            if(it.isSuccessful){
                _followerList.postValue(Resource.success(it.body()))
            }else{
                _followerList.postValue(Resource.error(it.errorBody().toString(),null))
            }
        }
    }

    fun requestListAllFollowing(memberId: String) = viewModelScope.launch {
        followRepository.requestListAllFollowing(memberId).let {
            if(it.isSuccessful){
                _followingList.postValue(Resource.success(it.body()))
            }else{
                _followingList.postValue(Resource.error(it.errorBody().toString(),null))
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