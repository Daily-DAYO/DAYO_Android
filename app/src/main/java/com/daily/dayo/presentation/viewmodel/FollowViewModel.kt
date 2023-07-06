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
import com.daily.dayo.domain.model.NetworkResponse
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

    var memberId: String = ""

    private val _followSuccess = MutableLiveData<Event<Boolean>>()
    val followSuccess: LiveData<Event<Boolean>> get() = _followSuccess

    private val _unfollowSuccess = MutableLiveData<Event<Boolean>>()
    val unfollowSuccess: LiveData<Event<Boolean>> get() = _unfollowSuccess

    private val _followerList = MutableLiveData<Resource<List<Follow>>>()
    val followerList: LiveData<Resource<List<Follow>>> get() = _followerList

    private val _followerCount = MutableLiveData<Resource<Int>>()
    val followerCount: LiveData<Resource<Int>> get() = _followerCount

    private val _followingList = MutableLiveData<Resource<List<Follow>>>()
    val followingList: LiveData<Resource<List<Follow>>> get() = _followingList

    private val _followingCount = MutableLiveData<Resource<Int>>()
    val followingCount: LiveData<Resource<Int>> get() = _followingCount

    fun requestListAllFollower(memberId: String) = viewModelScope.launch {
        requestListAllFollowerUseCase(memberId = memberId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _followerCount.postValue(Resource.success(ApiResponse.body?.count))
                    val myInfo = ApiResponse.body
                        ?.data
                        ?.find { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                        ?.toFollow()
                    val tmpFollowerList = ApiResponse.body?.data?.map { it.toFollow() }
                        ?.filterNot { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                        ?.toMutableList()
                    if (myInfo != null) tmpFollowerList?.add(0, myInfo)
                    _followerList.postValue(Resource.success(tmpFollowerList))
                }
                is NetworkResponse.NetworkError -> {
                    _followerList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }
                is NetworkResponse.ApiError -> {
                    _followerList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }
                is NetworkResponse.UnknownError -> {
                    _followerList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestListAllFollowing(memberId: String) = viewModelScope.launch {
        requestListAllFollowingUseCase(memberId = memberId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _followingCount.postValue(Resource.success(ApiResponse.body?.count))
                    val myInfo = ApiResponse.body
                        ?.data
                        ?.find { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                        ?.toFollow()
                    val tmpFollowingList = ApiResponse.body?.data?.map { it.toFollow() }
                        ?.filterNot { it.memberId == DayoApplication.preferences.getCurrentUser().memberId }
                        ?.toMutableList()
                    if (myInfo != null) tmpFollowingList?.add(0, myInfo)
                    _followingList.postValue(Resource.success(tmpFollowingList))
                }
                is NetworkResponse.NetworkError -> {
                    _followingList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }
                is NetworkResponse.ApiError -> {
                    _followingList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }
                is NetworkResponse.UnknownError -> {
                    _followingList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestCreateFollow(followerId: String) = viewModelScope.launch {
        requestCreateFollowUseCase(CreateFollowRequest(followerId = followerId)).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _followSuccess.postValue(Event(true))
                }
                else -> {
                    _followSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestDeleteFollow(followerId: String) = viewModelScope.launch {
        requestDeleteFollowUseCase(followerId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _unfollowSuccess.postValue(Event(true))
                }
                else -> {
                    _unfollowSuccess.postValue(Event(false))
                }
            }
        }
    }
}