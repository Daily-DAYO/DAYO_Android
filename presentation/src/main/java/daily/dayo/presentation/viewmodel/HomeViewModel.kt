package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Post
import daily.dayo.domain.usecase.like.RequestLikePostUseCase
import daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import daily.dayo.domain.usecase.post.RequestDayoPickPostListCategoryUseCase
import daily.dayo.domain.usecase.post.RequestDayoPickPostListUseCase
import daily.dayo.domain.usecase.post.RequestNewPostListCategoryUseCase
import daily.dayo.domain.usecase.post.RequestNewPostListUseCase
import daily.dayo.presentation.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val requestNewPostListUseCase: RequestNewPostListUseCase,
    private val requestNewPostListCategoryUseCase: RequestNewPostListCategoryUseCase,
    private val requestHomeDayoPickPostListUseCase: RequestDayoPickPostListUseCase,
    private val requestDayoPickPostListCategoryUseCase: RequestDayoPickPostListCategoryUseCase,
    private val requestLikePostUseCase: RequestLikePostUseCase,
    private val requestUnlikePostUseCase: RequestUnlikePostUseCase
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _currentCategory = MutableStateFlow(Category.ALL)
    val currentCategory: StateFlow<Category>
        get() = _currentCategory.asStateFlow()

    private val _dayoPickPostList = MutableLiveData<Resource<List<Post>>>()
    val dayoPickPostList: LiveData<Resource<List<Post>>> get() = _dayoPickPostList

    private val _newPostList = MutableLiveData<Resource<List<Post>>>()
    val newPostList: LiveData<Resource<List<Post>>> get() = _newPostList

    fun setCategory(category: Category) {
        viewModelScope.launch {
            _currentCategory.emit(category)
        }
    }

    fun loadDayoPickPosts() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            requestDayoPickPostList()
            _isRefreshing.emit(false)
        }
    }

    fun loadNewPosts() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            requestNewPostList()
            _isRefreshing.emit(false)
        }
    }

    fun requestDayoPickPostList() = viewModelScope.launch {
        _dayoPickPostList.postValue(Resource.loading(null))
        when (currentCategory.value) {
            Category.ALL -> requestHomeDayoPickPostList()
            else -> requestHomeDayoPickPostListCategory(currentCategory.value)
        }
    }

    fun requestNewPostList() = viewModelScope.launch {
        _newPostList.postValue(Resource.loading(null))
        when (currentCategory.value) {
            Category.ALL -> requestHomeNewPostList()
            else -> requestHomeNewPostListCategory(currentCategory.value)
        }
    }

    private fun requestHomeNewPostList() = viewModelScope.launch {
        requestNewPostListUseCase()?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _newPostList.postValue(Resource.success(ApiResponse.body?.data))
                }

                is NetworkResponse.NetworkError -> {
                    _newPostList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _newPostList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _newPostList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    private fun requestHomeNewPostListCategory(category: Category) = viewModelScope.launch {
        requestNewPostListCategoryUseCase(category = category)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _newPostList.postValue(Resource.success(ApiResponse.body?.data))
                }

                is NetworkResponse.NetworkError -> {
                    _newPostList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _newPostList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _newPostList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    private fun requestHomeDayoPickPostList() = viewModelScope.launch {
        requestHomeDayoPickPostListUseCase()?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _dayoPickPostList.postValue(Resource.success(ApiResponse.body?.data))
                }

                is NetworkResponse.NetworkError -> {
                    _dayoPickPostList.postValue(
                        Resource.error(
                            ApiResponse.exception.toString(),
                            null
                        )
                    )
                }

                is NetworkResponse.ApiError -> {
                    _dayoPickPostList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _dayoPickPostList.postValue(
                        Resource.error(
                            ApiResponse.throwable.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    private fun requestHomeDayoPickPostListCategory(category: Category) = viewModelScope.launch {
        requestDayoPickPostListCategoryUseCase(category = category)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _dayoPickPostList.postValue(Resource.success(ApiResponse.body?.data))
                }

                is NetworkResponse.NetworkError -> {
                    _dayoPickPostList.postValue(
                        Resource.error(
                            ApiResponse.exception.toString(),
                            null
                        )
                    )
                }

                is NetworkResponse.ApiError -> {
                    _dayoPickPostList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _dayoPickPostList.postValue(
                        Resource.error(
                            ApiResponse.throwable.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun requestLikePost(postId: Long, isDayoPickLike: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            val editList = if (isDayoPickLike) _dayoPickPostList else _newPostList
            requestLikePostUseCase(postId = postId).let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        editList.postValue(
                            Resource.success(
                                editList.value?.data?.map {
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
                        )
                    }

                    is NetworkResponse.NetworkError -> {}
                    is NetworkResponse.ApiError -> {}
                    is NetworkResponse.UnknownError -> {}
                }
            }
        }

    fun requestUnlikePost(postId: Long, isDayoPickLike: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            val editList = if (isDayoPickLike) _dayoPickPostList else _newPostList
            requestUnlikePostUseCase(postId = postId).let { ApiResponse ->
                when (ApiResponse) {
                    is NetworkResponse.Success -> {
                        editList.postValue(
                            Resource.success(
                                editList.value?.data?.map {
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
                        )
                    }

                    is NetworkResponse.NetworkError -> {}
                    is NetworkResponse.ApiError -> {}
                    is NetworkResponse.UnknownError -> {}
                }
            }
        }

    fun setPostStatus(
        postId: Long,
        isLike: Boolean? = null,
        heartCount: Int? = null,
        commentCount: Int? = null
    ) {
        _dayoPickPostList.postValue(
            Resource.success(
                _dayoPickPostList.value?.data?.map {
                    if (it.postId == postId) {
                        it.copy(
                            heart = isLike ?: it.heart,
                            heartCount = heartCount ?: it.heartCount,
                            commentCount = commentCount ?: it.commentCount
                        )
                    } else {
                        it
                    }
                }
            )
        )
        _newPostList.postValue(
            Resource.success(
                _newPostList.value?.data?.map {
                    if (it.postId == postId) {
                        it.copy(
                            heart = isLike ?: it.heart,
                            heartCount = heartCount ?: it.heartCount,
                            commentCount = commentCount ?: it.commentCount
                        )
                    } else {
                        it
                    }
                }
            )
        )
    }
}