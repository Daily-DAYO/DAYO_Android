package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import daily.dayo.presentation.common.Resource
import daily.dayo.domain.model.BookmarkPostResponse
import daily.dayo.domain.model.LikePostResponse
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Post
import daily.dayo.domain.usecase.bookmark.RequestBookmarkPostUseCase
import daily.dayo.domain.usecase.bookmark.RequestDeleteBookmarkPostUseCase
import daily.dayo.domain.usecase.like.RequestLikePostUseCase
import daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import daily.dayo.domain.usecase.post.RequestFeedListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val requestFeedListUseCase: RequestFeedListUseCase,
    private val requestLikePostUseCase: RequestLikePostUseCase,
    private val requestUnlikePostUseCase: RequestUnlikePostUseCase,
    private val requestBookmarkPostUseCase: RequestBookmarkPostUseCase,
    private val requestDeleteBookmarkPostUseCase: RequestDeleteBookmarkPostUseCase
) : ViewModel() {

    private val _feedList = MutableLiveData<PagingData<Post>>()
    val feedList: LiveData<PagingData<Post>> get() = _feedList

    private val _postLiked = MutableLiveData<Resource<LikePostResponse>>()
    val postLiked: LiveData<Resource<LikePostResponse>> get() = _postLiked

    private val _postBookmarked = MutableLiveData<Resource<BookmarkPostResponse>>()
    val postBookmarked: LiveData<Resource<BookmarkPostResponse>> get() = _postBookmarked

    fun requestFeedList() = viewModelScope.launch(Dispatchers.IO) {
        requestFeedListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _feedList.postValue(it) }
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        requestLikePostUseCase(postId = postId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _feedList.postValue(
                        _feedList.value?.map {
                            if (it.postId == postId) {
                                it.copy(
                                    heart = true,
                                    heartCount = ApiResponse.body?.allCount ?: 0
                                )
                            } else {
                                it
                            }
                        }
                    )
                    _postLiked.postValue(Resource.success(ApiResponse.body))
                }
                is NetworkResponse.NetworkError -> { _postLiked.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _postLiked.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _postLiked.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        requestUnlikePostUseCase(postId = postId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _feedList.postValue(
                        _feedList.value?.map {
                            if (it.postId == postId) {
                                it.copy(
                                    heart = false,
                                    heartCount = ApiResponse.body?.allCount ?: 0
                                )
                            } else {
                                it
                            }
                        }
                    )
                }
                else -> {}
            }
        }
    }

    fun requestBookmarkPost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        requestBookmarkPostUseCase(postId = postId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _feedList.postValue(
                        _feedList.value?.map {
                            if (it.postId == postId) {
                                it.copy(
                                    bookmark = true
                                )
                            } else {
                                it
                            }
                        }
                    )
                    _postBookmarked.postValue(Resource.success(ApiResponse.body))
                }
                is NetworkResponse.NetworkError -> { _postBookmarked.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _postBookmarked.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _postBookmarked.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }

    fun requestDeleteBookmarkPost(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        requestDeleteBookmarkPostUseCase(postId = postId)?.let { ApiResponse ->
            when(ApiResponse) {
                is NetworkResponse.Success -> {
                    _feedList.postValue(
                        _feedList.value?.map {
                            if (it.postId == postId) {
                                it.copy(
                                    bookmark = false
                                )
                            } else {
                                it
                            }
                        }
                    )
                }
                else -> {}
            }
        }
    }
}