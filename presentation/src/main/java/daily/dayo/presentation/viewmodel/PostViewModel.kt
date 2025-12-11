package daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.Comments
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
import daily.dayo.domain.usecase.member.RequestCurrentUserInfoUseCase
import daily.dayo.domain.usecase.post.RequestDeletePostUseCase
import daily.dayo.domain.usecase.post.RequestPostDetailUseCase
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val requestPostLikeUsersUseCase: RequestPostLikeUsersUseCase,
    private val requestCurrentUserInfoUseCase: RequestCurrentUserInfoUseCase
) : ViewModel() {

    private val _postDetail = MutableLiveData<Resource<PostDetail>>()
    val postDetail: LiveData<Resource<PostDetail>> get() = _postDetail

    private val _postComments = MutableLiveData<Resource<Comments>>()
    val postComments: LiveData<Resource<Comments>> = _postComments

    private val _postDeleteSuccess = MutableSharedFlow<Status>()
    val postDeleteSuccess = _postDeleteSuccess.asSharedFlow()

    private val _postCommentCreateState = MutableLiveData<Resource<Boolean>>()
    val postCommentCreateState: LiveData<Resource<Boolean>> get() = _postCommentCreateState

    private val _postCommentDeleteSuccess = MutableLiveData<Event<Boolean>>()
    val postCommentDeleteSuccess get() = _postCommentDeleteSuccess

    private val _blockSuccess = MutableLiveData<Event<Boolean>>()
    val blockSuccess: LiveData<Event<Boolean>> get() = _blockSuccess

    private val _postLikeUsers = MutableStateFlow<PagingData<LikeUser>>(PagingData.empty())
    val postLikeUsers = _postLikeUsers.asStateFlow()

    val postLikeCountUiState: MutableStateFlow<Int> = MutableStateFlow(0)

    fun getCurrentUserInfo() = requestCurrentUserInfoUseCase()

    fun cleanUpPostDetail() {
        _postDetail.postValue(Resource.loading(null))
        _postComments.postValue(Resource.loading(null))
    }

    fun requestPostDetail(postId: Long) {
        viewModelScope.launch {
            _postDetail.postValue(Resource.loading(null))
            requestPostDetailUseCase(postId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _postDetail.postValue(Resource.success(response.body))
                        postLikeCountUiState.value = response.body?.heartCount ?: 0
                    }

                    is NetworkResponse.NetworkError -> {
                        _postDetail.postValue(Resource.error(response.exception.toString(), null))
                    }

                    is NetworkResponse.ApiError -> {
                        _postDetail.postValue(Resource.error(response.error.toString(), null))
                    }

                    is NetworkResponse.UnknownError -> {
                        _postDetail.postValue(Resource.error(response.throwable.toString(), null))
                    }
                }
            }
        }
    }

    fun requestDeletePost(postId: Long) {
        viewModelScope.launch {
            _postDeleteSuccess.emit(Status.LOADING)
            requestDeletePostUseCase(postId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> _postDeleteSuccess.emit(Status.SUCCESS)
                    else -> _postDeleteSuccess.emit(Status.ERROR)
                }
            }
        }
    }

    fun toggleLikePost(postId: Long, currentHeart: Boolean) {
        viewModelScope.launch {
            if (currentHeart) {
                requestUnlikePostUseCase(postId = postId)
            } else {
                requestLikePostUseCase(postId = postId)
            }.let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _postDetail.postValue(
                            Resource.success(
                                _postDetail.value?.data?.copy(
                                    heart = !currentHeart,
                                    heartCount = response.body?.allCount ?: 0
                                )
                            )
                        )
                    }

                    is NetworkResponse.NetworkError -> {
                        _postDetail.postValue(Resource.error(response.exception.toString(), null))
                    }

                    is NetworkResponse.ApiError -> {
                        _postDetail.postValue(Resource.error(response.error.toString(), null))
                    }

                    is NetworkResponse.UnknownError -> {
                        _postDetail.postValue(Resource.error(response.throwable.toString(), null))
                    }
                }
            }
        }
    }

    fun toggleBookmarkPostDetail(postId: Long, currentBookmark: Boolean?) {
        viewModelScope.launch {
            currentBookmark?.let { bookmark ->
                if (bookmark) {
                    requestDeleteBookmarkPostUseCase(postId = postId)
                } else {
                    requestBookmarkPostUseCase(postId = postId)
                }.let { response ->
                    when (response) {
                        is NetworkResponse.Success -> {
                            _postDetail.postValue(
                                Resource.success(
                                    _postDetail.value?.data?.copy(
                                        bookmark = !currentBookmark
                                    )
                                )
                            )
                        }

                        is NetworkResponse.NetworkError -> {
                            _postDetail.postValue(Resource.error(response.exception.toString(), null))
                        }

                        is NetworkResponse.ApiError -> {
                            _postDetail.postValue(Resource.error(response.error.toString(), null))
                        }

                        is NetworkResponse.UnknownError -> {
                            _postDetail.postValue(Resource.error(response.throwable.toString(), null))
                        }
                    }
                }
            }
        }
    }

    fun requestPostComment(postId: Long) {
        viewModelScope.launch {
            _postComments.postValue(Resource.loading(null))
            requestPostCommentUseCase(postId).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _postComments.postValue(Resource.success(response.body))
                    }

                    is NetworkResponse.NetworkError -> {
                        _postComments.postValue(Resource.error(response.exception.toString(), null))
                    }

                    is NetworkResponse.ApiError -> {
                        _postComments.postValue(Resource.error(response.error.toString(), null))
                    }

                    is NetworkResponse.UnknownError -> {
                        _postComments.postValue(Resource.error(response.throwable.toString(), null))
                    }
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

    fun requestCreatePostComment(contents: String, postId: Long, mentionedUser: List<SearchUser>) {
        if (contents.isEmpty() || _postCommentCreateState.value?.status == Status.LOADING) return

        viewModelScope.launch {
            _postCommentCreateState.postValue(Resource.loading(null))
            val mentionList = getMentionList(contents, mentionedUser)
            requestCreatePostCommentUseCase(contents = contents, postId = postId, mentionList = mentionList).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _postCommentCreateState.postValue(Resource.success(true))
                    }

                    else -> {
                        _postCommentCreateState.postValue(Resource.error("댓글 작성 실패", false))
                    }
                }
            }
        }
    }

    fun requestCreatePostCommentReply(reply: Pair<Long, Comment>, contents: String, postId: Long, mentionedUser: List<SearchUser>) {
        if (contents.isEmpty() || _postCommentCreateState.value?.status == Status.LOADING) return

        viewModelScope.launch {
            _postCommentCreateState.postValue(Resource.loading(null))
            val mentionList = getMentionList(contents, mentionedUser).toMutableList()
            val (parentCommentId, comment) = reply
            mentionList.add(MentionUser(comment.memberId, comment.nickname)) // 언급된 유저 리스트에 원본 댓글 유저 추가 (팔로우하지 않아도 답글 가능하므로 따로 추가)
            requestCreatePostCommentReplyUseCase(commentId = parentCommentId, contents = contents, postId = postId, mentionList = mentionList).let { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _postCommentCreateState.postValue(Resource.success(true))
                    }

                    else -> {
                        _postCommentCreateState.postValue(Resource.error("답글 작성 실패", false))
                    }
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

    fun requestPostLikeUsers(postId: Long) = viewModelScope.launch(Dispatchers.IO) {
        requestPostLikeUsersUseCase(postId = postId)
            .cachedIn(viewModelScope)
            .collectLatest { _postLikeUsers.emit(it) }
    }
}