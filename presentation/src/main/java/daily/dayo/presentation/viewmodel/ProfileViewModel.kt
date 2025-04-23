package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.Folder
import daily.dayo.domain.model.LikePost
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Profile
import daily.dayo.domain.usecase.block.RequestBlockMemberUseCase
import daily.dayo.domain.usecase.block.RequestUnblockMemberUseCase
import daily.dayo.domain.usecase.folder.RequestAllFolderListUseCase
import daily.dayo.domain.usecase.folder.RequestAllMyFolderListUseCase
import daily.dayo.domain.usecase.follow.RequestCreateFollowUseCase
import daily.dayo.domain.usecase.follow.RequestDeleteFollowUseCase
import daily.dayo.domain.usecase.like.RequestAllMyLikePostListUseCase
import daily.dayo.domain.usecase.member.RequestCurrentUserInfoUseCase
import daily.dayo.domain.usecase.member.RequestMyProfileUseCase
import daily.dayo.domain.usecase.member.RequestOtherProfileUseCase
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val requestCurrentUserInfoUseCase: RequestCurrentUserInfoUseCase,
    private val requestAllFolderListUseCase: RequestAllFolderListUseCase,
    private val requestAllMyFolderListUseCase: RequestAllMyFolderListUseCase,
    private val requestMyProfileUseCase: RequestMyProfileUseCase,
    private val requestOtherProfileUseCase: RequestOtherProfileUseCase,
    private val requestCreateFollowUseCase: RequestCreateFollowUseCase,
    private val requestDeleteFollowUseCase: RequestDeleteFollowUseCase,
    private val requestAllMyLikePostListUseCase: RequestAllMyLikePostListUseCase,
    private val requestBlockMemberUseCase: RequestBlockMemberUseCase,
    private val requestUnblockMemberUseCase: RequestUnblockMemberUseCase
) : ViewModel() {

    val currentMemberId get() = requestCurrentUserInfoUseCase().memberId

    private val _profileInfo = MutableLiveData<Resource<Profile>>()
    val profileInfo: LiveData<Resource<Profile>> = _profileInfo

    private val _followSuccess = MutableSharedFlow<Boolean>()
    val followSuccess = _followSuccess.asSharedFlow()

    private val _unfollowSuccess = MutableSharedFlow<Boolean>()
    val unfollowSuccess = _unfollowSuccess.asSharedFlow()

    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList

    private val _folders = MutableStateFlow<Resource<List<Folder>>>(Resource.loading(null))
    val folders get() = _folders.asStateFlow()

    private val _likePostList = MutableLiveData<PagingData<LikePost>>()
    val likePostList: LiveData<PagingData<LikePost>> get() = _likePostList

    private val _blockSuccess = MutableLiveData<Event<Boolean>>()
    val blockSuccess: LiveData<Event<Boolean>> get() = _blockSuccess

    private val _unblockSuccess = MutableStateFlow<Status?>(null)
    val unblockSuccess: StateFlow<Status?> get() = _unblockSuccess

    fun requestMyProfile() = viewModelScope.launch {
        requestMyProfileUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _profileInfo.postValue(Resource.success(ApiResponse.body))
                }

                is NetworkResponse.NetworkError -> {

                }

                is NetworkResponse.ApiError -> {

                }

                is NetworkResponse.UnknownError -> {

                }
            }
        }
    }

    fun requestOtherProfile(memberId: String) {
        viewModelScope.launch {
            requestOtherProfileUseCase(memberId = memberId).let { apiResponse ->
                when (apiResponse) {
                    is NetworkResponse.Success -> {
                        _profileInfo.value = Resource.success(apiResponse.body)
                    }

                    else -> _profileInfo.value = Resource.error(apiResponse.toString(), null)
                }
            }
        }
    }

    private fun requestFollow(followerId: String) {
        viewModelScope.launch {
            requestCreateFollowUseCase(followerId = followerId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _followSuccess.emit(true)
                    }

                    else -> {
                        _followSuccess.emit(false)
                    }
                }
            }
        }
    }

    private fun requestUnfollow(followerId: String) {
        viewModelScope.launch {
            requestDeleteFollowUseCase(followerId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _unfollowSuccess.emit(true)
                    }

                    else -> {
                        _unfollowSuccess.emit(false)
                    }
                }
            }
        }
    }

    fun toggleFollow(memberId: String, isFollow: Boolean) {
        viewModelScope.launch {
            if (isFollow) {
                requestUnfollow(memberId)
            } else {
                requestFollow(memberId)
            }
        }
    }

    fun requestFolderList(memberId: String, isMine: Boolean) = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        _folders.emit(Resource.loading(null))
        if (isMine) {
            requestAllMyFolderListUseCase()?.let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        _folderList.postValue(Resource.success(ApiResponse.body?.data))
                        _folders.emit(Resource.success(ApiResponse.body?.data))
                    }

                    is NetworkResponse.NetworkError -> {
                        _folderList.postValue(
                            Resource.error(
                                ApiResponse.exception.toString(),
                                null
                            )
                        )
                        _folders.emit(
                            Resource.error(
                                ApiResponse.exception.toString(),
                                null
                            )
                        )
                    }

                    is NetworkResponse.ApiError -> {
                        _folderList.postValue(Resource.error(ApiResponse.error.toString(), null))
                        _folders.emit(Resource.error(ApiResponse.error.toString(), null))
                    }

                    is NetworkResponse.UnknownError -> {
                        _folderList.postValue(
                            Resource.error(
                                ApiResponse.throwable.toString(),
                                null
                            )
                        )
                        _folders.emit(
                            Resource.error(
                                ApiResponse.throwable.toString(),
                                null
                            )
                        )
                    }
                }
            }
        } else {
            requestAllFolderListUseCase(memberId = memberId)?.let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        _folderList.postValue(Resource.success(ApiResponse.body?.data))
                        _folders.emit(Resource.success(ApiResponse.body?.data))
                    }

                    is NetworkResponse.NetworkError -> {
                        _folderList.postValue(
                            Resource.error(
                                ApiResponse.exception.toString(),
                                null
                            )
                        )
                        _folders.emit(
                            Resource.error(
                                ApiResponse.exception.toString(),
                                null
                            )
                        )
                    }

                    is NetworkResponse.ApiError -> {
                        _folderList.postValue(Resource.error(ApiResponse.error.toString(), null))
                        _folders.emit(Resource.error(ApiResponse.error.toString(), null))
                    }

                    is NetworkResponse.UnknownError -> {
                        _folderList.postValue(
                            Resource.error(
                                ApiResponse.throwable.toString(),
                                null
                            )
                        )
                        _folders.emit(
                            Resource.error(
                                ApiResponse.throwable.toString(),
                                null
                            )
                        )
                    }
                }
            }
        }
    }

    fun requestAllMyLikePostList() = viewModelScope.launch {
        requestAllMyLikePostListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _likePostList.postValue(it) }
    }

    fun requestBlockMember(memberId: String) = viewModelScope.launch {
        requestBlockMemberUseCase(memberId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _blockSuccess.postValue(Event(true))
                }

                is NetworkResponse.NetworkError -> {
                    _blockSuccess.postValue(Event(false))
                }

                is NetworkResponse.ApiError -> {
                    _blockSuccess.postValue(Event(false))
                }

                is NetworkResponse.UnknownError -> {
                    _blockSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestUnblockMember(memberId: String) = viewModelScope.launch {
        _unblockSuccess.value = Status.LOADING
        requestUnblockMemberUseCase(memberId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _unblockSuccess.emit(Status.SUCCESS)
                }

                is NetworkResponse.NetworkError -> {
                    _unblockSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.ApiError -> {
                    _unblockSuccess.emit(Status.ERROR)
                }

                is NetworkResponse.UnknownError -> {
                    _unblockSuccess.emit(Status.ERROR)
                }
            }
        }
    }

    fun cleanUpFolders() {
        viewModelScope.launch {
            _profileInfo.postValue(Resource.loading(null))
            _folderList.postValue(Resource.loading(null))
            _folders.emit(Resource.loading(null))
        }
    }
}