package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.follow.CreateFollowRequest
import com.daily.dayo.data.mapper.toFollow
import com.daily.dayo.domain.model.Follow
import com.daily.dayo.domain.usecase.follow.RequestCreateFollowUseCase
import com.daily.dayo.domain.usecase.follow.RequestDeleteFollowUseCase
import com.daily.dayo.domain.usecase.follow.RequestListAllFollowerUseCase
import com.daily.dayo.domain.usecase.follow.RequestListAllFollowingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val requestListAllFollowerUseCase: RequestListAllFollowerUseCase,
    private val requestListAllFollowingUseCase: RequestListAllFollowingUseCase,
    private val requestCreateFollowUseCase: RequestCreateFollowUseCase,
    private val requestDeleteFollowUseCase: RequestDeleteFollowUseCase
) : ViewModel() {

    private val _followSuccess = MutableLiveData<Event<Boolean>>()
    val followSuccess: LiveData<Event<Boolean>> get() = _followSuccess

    private val _unfollowSuccess = MutableLiveData<Event<Boolean>>()
    val unfollowSuccess: LiveData<Event<Boolean>> get() = _unfollowSuccess

    private val _memberId = MutableLiveData<String>()
    val memberId: LiveData<String> get() = _memberId

    private val _followerList = MutableLiveData<Resource<List<Follow>>>()
    val followerList: LiveData<Resource<List<Follow>>> get() = _followerList

    private val _followerCount = MutableLiveData<Resource<Int>>()
    val followerCount: LiveData<Resource<Int>> get() = _followerCount

    private val _followingList = MutableLiveData<Resource<List<Follow>>>()
    val followingList: LiveData<Resource<List<Follow>>> get() = _followingList

    private val _followingCount = MutableLiveData<Resource<Int>>()
    val followingCount: LiveData<Resource<Int>> get() = _followingCount

    fun setMemberId(id: String) {
        _memberId.value = id
    }

    fun requestListAllFollower(memberId: String) = viewModelScope.launch {
        val response = requestListAllFollowerUseCase(memberId = memberId)
        if (response.isSuccessful) {
            _followerCount.postValue(Resource.success(response.body()?.count))
            val myInfo = response.body()
                ?.data
                ?.find { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                ?.toFollow()
            val tmpFollowerList = response.body()?.data?.map { it.toFollow() }
                ?.filterNot { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                ?.toMutableList()
            if (myInfo != null) tmpFollowerList?.add(0, myInfo)
            _followerList.postValue(Resource.success(tmpFollowerList))
        } else {
            _followerList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestListAllFollowing(memberId: String) = viewModelScope.launch {
        val response = requestListAllFollowingUseCase(memberId = memberId)
        if (response.isSuccessful) {
            _followingCount.postValue(Resource.success(response.body()?.count))
            val myInfo = response.body()
                ?.data
                ?.find { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                ?.toFollow()
            val tmpFollowingList = response.body()?.data?.map { it.toFollow() }
                ?.filterNot { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                ?.toMutableList()
            if (myInfo != null) tmpFollowingList?.add(0, myInfo)
            _followingList.postValue(Resource.success(tmpFollowingList))
        } else {
            _followingList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestCreateFollow(followerId: String) = viewModelScope.launch {
        requestCreateFollowUseCase(CreateFollowRequest(followerId = followerId)).let {
            if (it.isSuccessful) {
                _followSuccess.postValue(Event(true))
            } else {
                _followSuccess.postValue(Event(false))
            }
        }
    }

    fun requestDeleteFollow(followerId: String) = viewModelScope.launch {
        requestDeleteFollowUseCase(followerId).let {
            if (it.isSuccessful) {
                _unfollowSuccess.postValue(Event(true))
            } else {
                _unfollowSuccess.postValue(Event(false))
            }
        }
    }
}