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

    private val _followerUnfollowSuccess = MutableLiveData<Event<Boolean>>()
    val followerUnfollowSuccess: LiveData<Event<Boolean>> = _followerUnfollowSuccess

    private val _followingFollowSuccess = MutableLiveData<Event<Boolean>>()
    val followingFollowSuccess: LiveData<Event<Boolean>> = _followingFollowSuccess

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

    fun requestFollow(followerId: String, isFollower: Boolean) {
        viewModelScope.launch {
            requestCreateFollowUseCase(followerId = followerId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        if (!isFollower) _followingFollowSuccess.postValue(Event(true))
                    }

                    else -> {
                        if (!isFollower) _followingFollowSuccess.postValue(Event(false))
                    }
                }
            }
        }
    }

    fun requestUnfollow(followerId: String, isFollower: Boolean) {
        viewModelScope.launch {
            requestDeleteFollowUseCase(followerId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        if (isFollower) _followerUnfollowSuccess.postValue(Event(true))
                    }

                    else -> {
                        if (isFollower) _followerUnfollowSuccess.postValue(Event(false))
                    }
                }
            }
        }
    }

    fun toggleFollow(follow: Follow, isFollower: Boolean) {
        viewModelScope.launch {
            if (follow.isFollow) {
                requestUnfollow(follow.memberId, isFollower)
            } else {
                requestFollow(follow.memberId, isFollower)
            }

            if (isFollower) {
                updateFollowUiState(follow, _followerUiState)
            } else {
                updateFollowUiState(follow, _followingUiState)
            }
        }
    }

    private fun updateFollowUiState(
        follow: Follow,
        followUiState: MutableLiveData<FollowUiState>,
    ) {
        val currentState = followUiState.value
        if (currentState is FollowUiState.Success) {
            val updatedData = currentState.data.map {
                if (it.memberId == follow.memberId) {
                    it.copy(isFollow = !it.isFollow)
                } else {
                    it
                }
            }

            followUiState.value = FollowUiState.Success(
                count = currentState.count,
                data = updatedData
            )
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
