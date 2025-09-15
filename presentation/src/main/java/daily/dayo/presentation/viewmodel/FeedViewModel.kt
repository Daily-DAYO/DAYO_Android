package daily.dayo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Post
import daily.dayo.domain.usecase.bookmark.RequestBookmarkPostUseCase
import daily.dayo.domain.usecase.bookmark.RequestDeleteBookmarkPostUseCase
import daily.dayo.domain.usecase.like.RequestLikePostUseCase
import daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import daily.dayo.domain.usecase.post.RequestFeedListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var _currentCategory = Category.ALL
    private val currentCategory get() = _currentCategory

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _feedPosts = MutableStateFlow<PagingData<Post>>(PagingData.empty())
    val feedPosts = _feedPosts.asStateFlow()

    fun loadFeedPosts() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            requestFeedList()
            _isRefreshing.emit(false)
        }
    }

    fun setCurrentCategory(category: Category) {
        _currentCategory = category
    }

    fun toggleLikePost(post: Post) {
        viewModelScope.launch {
            post.postId?.let { postId ->
                if (post.heart) {
                    requestUnlikePostUseCase(postId = postId)
                } else {
                    requestLikePostUseCase(postId = postId)
                }.let { response ->
                    when (response) {
                        is NetworkResponse.Success -> {
                            _feedPosts.emit(
                                _feedPosts.value.map {
                                    if (it.postId == post.postId) {
                                        it.copy(
                                            heart = !post.heart,
                                            heartCount = response.body?.allCount ?: 0
                                        )
                                    } else {
                                        it
                                    }
                                }
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    fun toggleBookmarkPost(post: Post) {
        viewModelScope.launch {
            post.postId?.let { postId ->
                post.bookmark?.let { bookmark ->
                    if (bookmark) {
                        requestDeleteBookmarkPostUseCase(postId = postId)
                    } else {
                        requestBookmarkPostUseCase(postId = postId)
                    }.let { response ->
                        when (response) {
                            is NetworkResponse.Success -> {
                                _feedPosts.emit(
                                    _feedPosts.value.map {
                                        if (it.postId == post.postId) {
                                            it.copy(
                                                bookmark = !bookmark
                                            )
                                        } else {
                                            it
                                        }
                                    }
                                )
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun requestFeedList() {
        viewModelScope.launch {
            requestFeedListUseCase(currentCategory)
                .cachedIn(viewModelScope)
                .collectLatest {
                    _feedPosts.emit(it)
                }
        }
    }
}
