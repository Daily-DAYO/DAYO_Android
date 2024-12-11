package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.Follow
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.usecase.follow.RequestCreateFollowUseCase
import daily.dayo.domain.usecase.follow.RequestDeleteFollowUseCase
import daily.dayo.domain.usecase.follow.RequestListAllFollowerUseCase
import daily.dayo.domain.usecase.follow.RequestListAllFollowingUseCase
import daily.dayo.domain.usecase.member.RequestCurrentUserInfoUseCase
import daily.dayo.presentation.common.Event
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

    private val _followerUiState = MutableLiveData<FollowUiState>()
    val followerUiState: LiveData<FollowUiState> = _followerUiState

    private val _followingUiState = MutableLiveData<FollowUiState>()
    val followingUiState: LiveData<FollowUiState> = _followingUiState

    fun requestFollowerList(memberId: String) {
        viewModelScope.launch {
            requestListAllFollowerUseCase(memberId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val result = response.body?.let { result ->
                            val followerList = result.data.sortedByDescending {
                                it.memberId == requestCurrentUserInfoUseCase().memberId
                            }
                            FollowUiState.Success(result.count, followerList)
                        } ?: FollowUiState.Success()
                        _followerUiState.postValue(result)
                    }

                    is NetworkResponse.NetworkError,
                    is NetworkResponse.ApiError,
                    is NetworkResponse.UnknownError -> {
                        _followerUiState.postValue(FollowUiState.Error)
                    }
                }
            }
        }
    }

    fun requestFollowingList(memberId: String) {
        viewModelScope.launch {
            requestListAllFollowingUseCase(memberId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val result = response.body?.let { result ->
                            val followingList = result.data.sortedByDescending {
                                it.memberId == requestCurrentUserInfoUseCase().memberId
                            }
                            FollowUiState.Success(result.count, followingList)
                        } ?: FollowUiState.Success()
                        _followingUiState.postValue(result)
                    }

                    is NetworkResponse.NetworkError,
                    is NetworkResponse.ApiError,
                    is NetworkResponse.UnknownError -> {
                        _followingUiState.postValue(FollowUiState.Error)
                    }
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

sealed class FollowUiState {
    object Loading : FollowUiState()
    data class Success(
        val count: Int = 0,
        val data: List<Follow> = emptyList()
    ) : FollowUiState()

    object Error : FollowUiState()
}
