package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.follow.CreateFollowRequest
import com.daily.dayo.data.mapper.toBookmarkPost
import com.daily.dayo.data.mapper.toFolder
import com.daily.dayo.data.mapper.toLikePost
import com.daily.dayo.data.mapper.toProfile
import com.daily.dayo.domain.model.*
import com.daily.dayo.domain.usecase.block.RequestBlockMemberUseCase
import com.daily.dayo.domain.usecase.bookmark.RequestAllMyBookmarkPostListUseCase
import com.daily.dayo.domain.usecase.folder.RequestAllFolderListUseCase
import com.daily.dayo.domain.usecase.folder.RequestAllMyFolderListUseCase
import com.daily.dayo.domain.usecase.follow.RequestCreateFollowUseCase
import com.daily.dayo.domain.usecase.follow.RequestDeleteFollowUseCase
import com.daily.dayo.domain.usecase.like.RequestAllMyLikePostListUseCase
import com.daily.dayo.domain.usecase.member.RequestOtherProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val requestAllFolderListUseCase: RequestAllFolderListUseCase,
    private val requestAllMyFolderListUseCase: RequestAllMyFolderListUseCase,
    private val requestOtherProfileUseCase: RequestOtherProfileUseCase,
    private val requestCreateFollowUseCase: RequestCreateFollowUseCase,
    private val requestDeleteFollowUseCase: RequestDeleteFollowUseCase,
    private val requestAllMyLikePostListUseCase: RequestAllMyLikePostListUseCase,
    private val requestAllMyBookmarkPostListUseCase: RequestAllMyBookmarkPostListUseCase,
    private val requestBlockMemberUseCase: RequestBlockMemberUseCase
) : ViewModel() {

    lateinit var profileMemberId: String

    private val _profileInfo = MutableLiveData<Profile>()
    val profileInfo: LiveData<Profile> get() = _profileInfo

    private val _followSuccess = MutableLiveData<Event<Boolean>>()
    val followSuccess: LiveData<Event<Boolean>> get() = _followSuccess

    private val _unfollowSuccess = MutableLiveData<Event<Boolean>>()
    val unfollowSuccess: LiveData<Event<Boolean>> get() = _unfollowSuccess

    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList

    private val _likePostList = MutableLiveData<Resource<List<LikePost>>>()
    val likePostList: LiveData<Resource<List<LikePost>>> get() = _likePostList

    private val _bookmarkPostList = MutableLiveData<Resource<List<BookmarkPost>>>()
    val bookmarkPostList: LiveData<Resource<List<BookmarkPost>>> get() = _bookmarkPostList

    private val _blockSuccess = MutableLiveData<Event<Boolean>>()
    val blockSuccess: LiveData<Event<Boolean>> get() = _blockSuccess

    fun requestProfile(memberId: String) = viewModelScope.launch {
        requestOtherProfileUseCase(memberId = memberId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _profileInfo.postValue(ApiResponse.body?.toProfile())
                }
                is NetworkResponse.NetworkError  -> {
                }
                is NetworkResponse.ApiError  -> {

                }
            }
        }
    }

    fun requestCreateFollow(followerId: String) = viewModelScope.launch {
        requestCreateFollowUseCase(CreateFollowRequest(followerId = followerId))?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _followSuccess.postValue(Event(true)) }
                else -> { _followSuccess.postValue(Event(false)) }
            }
        }
    }

    fun requestDeleteFollow(followerId: String) = viewModelScope.launch {
        requestDeleteFollowUseCase(followerId = followerId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _unfollowSuccess.postValue(Event(true)) }
                else -> { _unfollowSuccess.postValue(Event(false)) }
            }
        }
    }

    fun requestFolderList(memberId: String, isMine: Boolean) = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        if (isMine) {
            requestAllMyFolderListUseCase()?.let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> { _folderList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toFolder() })) }
                    is NetworkResponse.NetworkError -> { _folderList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                    is NetworkResponse.ApiError -> { _folderList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                    is NetworkResponse.UnknownError -> { _folderList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
                }
            }
        } else {
            requestAllFolderListUseCase(memberId = memberId)?.let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> { _folderList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toFolder() })) }
                    is NetworkResponse.NetworkError -> { _folderList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                    is NetworkResponse.ApiError -> { _folderList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                    is NetworkResponse.UnknownError -> { _folderList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
                }
            }
        }
    }

    fun requestAllMyLikePostList() = viewModelScope.launch {
        requestAllMyLikePostListUseCase()?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _likePostList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toLikePost() })) }
                is NetworkResponse.NetworkError -> { _likePostList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _likePostList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _likePostList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestAllMyBookmarkPostList() = viewModelScope.launch {
        requestAllMyBookmarkPostListUseCase()?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _bookmarkPostList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toBookmarkPost() })) }
                is NetworkResponse.NetworkError -> { _bookmarkPostList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _bookmarkPostList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _bookmarkPostList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestBlockMember(memberId: String) = viewModelScope.launch {
        requestBlockMemberUseCase(memberId)?.let {  ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _blockSuccess.postValue(Event(true)) }
                is NetworkResponse.NetworkError -> { _blockSuccess.postValue(Event(false)) }
                is NetworkResponse.ApiError -> { _blockSuccess.postValue(Event(false)) }
                is NetworkResponse.UnknownError -> { _blockSuccess.postValue(Event(false)) }
            }
        }
    }
}