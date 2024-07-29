package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import daily.dayo.domain.model.*
import daily.dayo.domain.usecase.block.RequestBlockMemberUseCase
import daily.dayo.domain.usecase.block.RequestUnblockMemberUseCase
import daily.dayo.domain.usecase.bookmark.RequestAllMyBookmarkPostListUseCase
import daily.dayo.domain.usecase.folder.RequestAllFolderListUseCase
import daily.dayo.domain.usecase.folder.RequestAllMyFolderListUseCase
import daily.dayo.domain.usecase.follow.RequestCreateFollowUseCase
import daily.dayo.domain.usecase.follow.RequestDeleteFollowUseCase
import daily.dayo.domain.usecase.like.RequestAllMyLikePostListUseCase
import daily.dayo.domain.usecase.member.RequestMyProfileUseCase
import daily.dayo.domain.usecase.member.RequestOtherProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val requestAllFolderListUseCase: RequestAllFolderListUseCase,
    private val requestAllMyFolderListUseCase: RequestAllMyFolderListUseCase,
    private val requestMyProfileUseCase: RequestMyProfileUseCase,
    private val requestOtherProfileUseCase: RequestOtherProfileUseCase,
    private val requestCreateFollowUseCase: RequestCreateFollowUseCase,
    private val requestDeleteFollowUseCase: RequestDeleteFollowUseCase,
    private val requestAllMyLikePostListUseCase: RequestAllMyLikePostListUseCase,
    private val requestAllMyBookmarkPostListUseCase: RequestAllMyBookmarkPostListUseCase,
    private val requestBlockMemberUseCase: RequestBlockMemberUseCase,
    private val requestUnblockMemberUseCase: RequestUnblockMemberUseCase
) : ViewModel() {

    lateinit var profileMemberId: String

    private val _profileInfo = MutableLiveData<Resource<Profile>>()
    val profileInfo: LiveData<Resource<Profile>> get() = _profileInfo

    private val _followSuccess = MutableLiveData<Event<Boolean>>()
    val followSuccess: LiveData<Event<Boolean>> get() = _followSuccess

    private val _unfollowSuccess = MutableLiveData<Event<Boolean>>()
    val unfollowSuccess: LiveData<Event<Boolean>> get() = _unfollowSuccess

    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList

    private val _folders = MutableStateFlow<Resource<List<Folder>>>(Resource.loading(null))
    val folders get() = _folders.asStateFlow()

    private val _likePostList = MutableLiveData<PagingData<LikePost>>()
    val likePostList: LiveData<PagingData<LikePost>> get() = _likePostList

    private val _bookmarkPostList = MutableLiveData<PagingData<BookmarkPost>>()
    val bookmarkPostList: LiveData<PagingData<BookmarkPost>> get() = _bookmarkPostList

    private val _blockSuccess = MutableLiveData<Event<Boolean>>()
    val blockSuccess: LiveData<Event<Boolean>> get() = _blockSuccess

    private val _unblockSuccess = MutableLiveData<Event<Boolean>>()
    val unblockSuccess: LiveData<Event<Boolean>> get() = _unblockSuccess

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

    fun requestOtherProfile(memberId: String) = viewModelScope.launch {
        requestOtherProfileUseCase(memberId = memberId).let { ApiResponse ->
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

    fun requestCreateFollow(followerId: String) = viewModelScope.launch {
        requestCreateFollowUseCase(followerId = followerId)?.let { ApiResponse ->
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
        requestDeleteFollowUseCase(followerId = followerId)?.let { ApiResponse ->
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

    fun requestAllMyBookmarkPostList() = viewModelScope.launch {
        requestAllMyBookmarkPostListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _bookmarkPostList.postValue(it) }
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
        requestUnblockMemberUseCase(memberId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _unblockSuccess.postValue(Event(true))
                }

                is NetworkResponse.NetworkError -> {
                    _unblockSuccess.postValue(Event(false))
                }

                is NetworkResponse.ApiError -> {
                    _unblockSuccess.postValue(Event(false))
                }

                is NetworkResponse.UnknownError -> {
                    _unblockSuccess.postValue(Event(false))
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