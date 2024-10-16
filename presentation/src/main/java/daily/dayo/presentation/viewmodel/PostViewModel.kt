package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.BookmarkPostResponse
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.LikePostResponse
import daily.dayo.domain.model.LikeUser
import daily.dayo.domain.model.MentionUser
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.PostDetail
import daily.dayo.domain.model.SearchUser
import daily.dayo.domain.usecase.block.RequestBlockMemberUseCase
import daily.dayo.domain.usecase.bookmark.RequestBookmarkPostUseCase
import daily.dayo.domain.usecase.bookmark.RequestDeleteBookmarkPostUseCase
import daily.dayo.domain.usecase.comment.RequestCreatePostCommentReplyUseCase
import daily.dayo.domain.usecase.comment.RequestCreatePostCommentUseCase
import daily.dayo.domain.usecase.comment.RequestDeletePostCommentUseCase
import daily.dayo.domain.usecase.comment.RequestPostCommentUseCase
import daily.dayo.domain.usecase.like.RequestLikePostUseCase
import daily.dayo.domain.usecase.like.RequestPostLikeUsersUseCase
import daily.dayo.domain.usecase.like.RequestUnlikePostUseCase
import daily.dayo.domain.usecase.post.RequestDeletePostUseCase
import daily.dayo.domain.usecase.post.RequestPostDetailUseCase
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val requestPostDetailUseCase: RequestPostDetailUseCase,
    private val requestDeletePostUseCase: RequestDeletePostUseCase,
    private val requestLikePostUseCase: RequestLikePostUseCase,
    private val requestUnlikePostUseCase: RequestUnlikePostUseCase,
    private val requestBookmarkPostUseCase: RequestBookmarkPostUseCase,
    private val requestDeleteBookmarkPostUseCase: RequestDeleteBookmarkPostUseCase,
    private val requestPostCommentUseCase: RequestPostCommentUseCase,
    private val requestCreatePostCommentUseCase: RequestCreatePostCommentUseCase,
    private val requestCreatePostCommentReplyUseCase: RequestCreatePostCommentReplyUseCase,
    private val requestDeletePostCommentUseCase: RequestDeletePostCommentUseCase,
    private val requestBlockMemberUseCase: RequestBlockMemberUseCase,
    private val requestPostLikeUsersUseCase: RequestPostLikeUsersUseCase
) : ViewModel() {

    private val _postDetail = MutableLiveData<Resource<PostDetail>>()
    val postDetail: LiveData<Resource<PostDetail>> get() = _postDetail

    private val _postLiked = MutableLiveData<Resource<LikePostResponse>>()
    val postLiked: LiveData<Resource<LikePostResponse>> get() = _postLiked

    private val _postBookmarked = MutableLiveData<Resource<BookmarkPostResponse>>()
    val postBookmarked: LiveData<Resource<BookmarkPostResponse>> get() = _postBookmarked

    private val _postCommentCreateSuccess = MutableLiveData<Event<Boolean>>()
    val postCommentCreateSuccess get() = _postCommentCreateSuccess

    private val _postCommentDeleteSuccess = MutableLiveData<Event<Boolean>>()
    val postCommentDeleteSuccess get() = _postCommentDeleteSuccess

    private val _postComment = MutableLiveData<Resource<List<Comment>>>()
    val postComment: LiveData<Resource<List<Comment>>> get() = _postComment

    private val _blockSuccess = MutableLiveData<Event<Boolean>>()
    val blockSuccess: LiveData<Event<Boolean>> get() = _blockSuccess

    private val _postLikeUsers = MutableStateFlow<PagingData<LikeUser>>(PagingData.empty())
    val postLikeUsers = _postLikeUsers.asStateFlow()

    val postLikeCountUiState: MutableStateFlow<Int> = MutableStateFlow(0)

    fun cleanUpPostDetail() {
        _postDetail.postValue(Resource.loading(null))
        _postComment.postValue(Resource.loading(null))
    }

    fun requestPostDetail(postId: Int) = viewModelScope.launch {
        _postDetail.postValue(Resource.loading(null))
        requestPostDetailUseCase(postId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postDetail.postValue(Resource.success(ApiResponse.body))
                    postLikeCountUiState.value = ApiResponse.body?.heartCount ?: 0
                }

                is NetworkResponse.NetworkError -> {
                    _postDetail.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _postDetail.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _postDetail.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestDeletePost(postId: Int) = viewModelScope.launch {
        requestDeletePostUseCase(postId)
    }

    fun requestLikePost(postId: Int) = viewModelScope.launch {
        requestLikePostUseCase(postId = postId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postDetail.postValue(
                        Resource.success(
                            _postDetail.value?.data?.apply {
                                heart = true
                                heartCount = ApiResponse.body?.allCount ?: 0
                            }
                        )
                    )
                }

                is NetworkResponse.NetworkError -> {
                    _postLiked.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _postLiked.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _postLiked.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        requestUnlikePostUseCase(postId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postDetail.postValue(
                        Resource.success(
                            _postDetail.value?.data?.apply {
                                heart = false
                                heartCount = ApiResponse.body?.allCount ?: 0
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

    fun requestBookmarkPost(postId: Int) = viewModelScope.launch {
        requestBookmarkPostUseCase(postId = postId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postBookmarked.postValue(Resource.success(ApiResponse.body))
                }

                is NetworkResponse.NetworkError -> {
                    _postBookmarked.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _postBookmarked.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _postBookmarked.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestDeleteBookmarkPost(postId: Int) = viewModelScope.launch {
        requestDeleteBookmarkPostUseCase(postId)
    }

    fun requestPostComment(postId: Int) = viewModelScope.launch {
        _postComment.postValue(Resource.loading(null))
        requestPostCommentUseCase(postId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postComment.postValue(Resource.success(ApiResponse.body?.data))
                }

                is NetworkResponse.NetworkError -> {
                    _postComment.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }

                is NetworkResponse.ApiError -> {
                    _postComment.postValue(Resource.error(ApiResponse.error.toString(), null))
                }

                is NetworkResponse.UnknownError -> {
                    _postComment.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    private fun getMentionList(contents: String, mentionedUser: List<SearchUser>): List<MentionUser> {
        val pattern = Pattern.compile("@\\w+")
        val matcher = pattern.matcher(contents)
        val usernames = mutableListOf<String>()
        while (matcher.find()) {
            usernames.add(matcher.group())
        }
        return mentionedUser.filter { user ->
            usernames.any {
                user.nickname == it.drop(1)
            }
        }.map {
            MentionUser(
                memberId = it.memberId,
                nickname = it.nickname
            )
        }
    }

    fun requestCreatePostComment(contents: String, postId: Int, mentionedUser: List<SearchUser>) = viewModelScope.launch {
        val mentionList = getMentionList(contents, mentionedUser)
        requestCreatePostCommentUseCase(contents = contents, postId = postId, mentionList = mentionList).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postCommentCreateSuccess.postValue(Event(true))
                }

                else -> {
                    _postCommentCreateSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestCreatePostCommentReply(reply: Pair<Long, Comment>, contents: String, postId: Int, mentionedUser: List<SearchUser>) = viewModelScope.launch {
        val mentionList = getMentionList(contents, mentionedUser).toMutableList()
        val (parentCommentId, comment) = reply
        mentionList.add(MentionUser(comment.memberId, comment.nickname)) // 언급된 유저 리스트에 원본 댓글 유저 추가 (팔로우하지 않아도 답글 가능하므로 따로 추가)
        requestCreatePostCommentReplyUseCase(commentId = parentCommentId, contents = contents, postId = postId, mentionList = mentionList).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postCommentCreateSuccess.postValue(Event(true))
                }

                else -> {
                    _postCommentCreateSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestDeletePostComment(commentId: Long) = viewModelScope.launch {
        requestDeletePostCommentUseCase(commentId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _postCommentDeleteSuccess.postValue(Event(true))
                }

                else -> {
                    _postCommentDeleteSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestBlockMember(memberId: String) = viewModelScope.launch {
        requestBlockMemberUseCase(memberId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _blockSuccess.postValue(Event(true))
                }

                else -> {
                    _blockSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestPostLikeUsers(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        requestPostLikeUsersUseCase(postId = postId)
            .cachedIn(viewModelScope)
            .collectLatest { _postLikeUsers.emit(it) }
    }
}