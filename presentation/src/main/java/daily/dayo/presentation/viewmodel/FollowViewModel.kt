package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import daily.dayo.domain.model.MyFollower
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.usecase.follow.RequestCreateFollowUseCase
import daily.dayo.domain.usecase.follow.RequestDeleteFollowUseCase
import daily.dayo.domain.usecase.follow.RequestListAllFollowerUseCase
import daily.dayo.domain.usecase.follow.RequestListAllFollowingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.usecase.member.RequestCurrentUserInfoUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val requestListAllFollowerUseCase: RequestListAllFollowerUseCase,
    private val requestListAllFollowingUseCase: RequestListAllFollowingUseCase,
    private val requestCreateFollowUseCase: RequestCreateFollowUseCase,
    private val requestDeleteFollowUseCase: RequestDeleteFollowUseCase,
    private val requestCurrentUserInfoUseCase: RequestCurrentUserInfoUseCase
) : ViewModel() {

    var memberId: String = ""

    private val _followerFollowSuccess = MutableLiveData<Event<Boolean>>()
    val followerFollowSuccess: LiveData<Event<Boolean>> get() = _followerFollowSuccess

    private val _followerUnfollowSuccess = MutableLiveData<Event<Boolean>>()
    val followerUnfollowSuccess: LiveData<Event<Boolean>> get() = _followerUnfollowSuccess

    private val _followingFollowSuccess = MutableLiveData<Event<Boolean>>()
    val followingFollowSuccess: LiveData<Event<Boolean>> get() = _followingFollowSuccess

    private val _followingUnfollowSuccess = MutableLiveData<Event<Boolean>>()
    val followingUnfollowSuccess: LiveData<Event<Boolean>> get() = _followingUnfollowSuccess

    private val _followerList = MutableLiveData<Resource<List<MyFollower>>>()
    val followerList: LiveData<Resource<List<MyFollower>>> get() = _followerList

    private val _followerCount = MutableLiveData<Resource<Int>>()
    val followerCount: LiveData<Resource<Int>> get() = _followerCount

    private val _followingList = MutableLiveData<Resource<List<MyFollower>>>()
    val followingList: LiveData<Resource<List<MyFollower>>> get() = _followingList

    private val _followingCount = MutableLiveData<Resource<Int>>()
    val followingCount: LiveData<Resource<Int>> get() = _followingCount

    fun requestListAllFollower(memberId: String) = viewModelScope.launch {
        requestListAllFollowerUseCase(memberId = memberId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _followerCount.postValue(Resource.success(ApiResponse.body?.count))
                    val myInfo = ApiResponse.body
                        ?.data
                        ?.find { it.memberId == requestCurrentUserInfoUseCase().memberId }
                    val tmpFollowerList = ApiResponse.body?.data
                        ?.filterNot { it.memberId == requestCurrentUserInfoUseCase().memberId }
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
                        ?.find { it.memberId == requestCurrentUserInfoUseCase().memberId }
                    val tmpFollowingList = ApiResponse.body?.data
                        ?.filterNot { it.memberId == requestCurrentUserInfoUseCase().memberId }
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

    fun requestCreateFollow(followerId: String, isFollower: Boolean) = viewModelScope.launch {
        requestCreateFollowUseCase(followerId = followerId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    if (isFollower) _followerFollowSuccess.postValue(Event(true))
                    else _followingFollowSuccess.postValue(Event(true))
                }
                else -> {
                    if (isFollower) _followerFollowSuccess.postValue(Event(false))
                    else _followingFollowSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestDeleteFollow(followerId: String, isFollower: Boolean) = viewModelScope.launch {
        requestDeleteFollowUseCase(followerId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    if (isFollower) _followerUnfollowSuccess.postValue(Event(true))
                    else _followingUnfollowSuccess.postValue(Event(true))
                }
                else -> {
                    if (isFollower) _followerUnfollowSuccess.postValue(Event(false))
                    else _followingUnfollowSuccess.postValue(Event(false))
                }
            }
        }
    }
}