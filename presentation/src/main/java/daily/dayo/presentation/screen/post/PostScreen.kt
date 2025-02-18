package daily.dayo.presentation.screen.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.PostDetail
import daily.dayo.domain.model.SearchUser
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.CommentListView
import daily.dayo.presentation.view.CommentMentionSearchView
import daily.dayo.presentation.view.CommentReplyDescriptionView
import daily.dayo.presentation.view.CommentTextField
import daily.dayo.presentation.view.DEFAULT_POST
import daily.dayo.presentation.view.DetailPostView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.CommentReportDialog
import daily.dayo.presentation.view.dialog.DEFAULT_COMMENTS
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.ReportViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun PostScreen(
    postId: String,
    snackBarHostState: SnackbarHostState,
    onProfileClick: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    onBackClick: () -> Unit,
    postViewModel: PostViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val postState = postViewModel.postDetail.observeAsState()
    var post by remember { mutableStateOf(DEFAULT_POST) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val commentFocusRequester = FocusRequester()

    // comment
    val commentState = postViewModel.postComments.observeAsState()
    val commentText = remember { mutableStateOf(TextFieldValue("")) }
    val showMentionSearchView = remember { mutableStateOf(false) }

    // comment option
    val onClickCommentDelete: (Long) -> Unit = { commentId ->
        postViewModel.requestDeletePostComment(commentId)
    }
    val postCommentDeleteSuccess by postViewModel.postCommentDeleteSuccess.observeAsState(Event(false))
    if (postCommentDeleteSuccess.getContentIfNotHandled() == true) {
        postViewModel.requestPostComment(postId.toInt())
        SideEffect {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.comment_delete_message))
            }
        }
    }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportCommentId by remember { mutableStateOf<Long?>(null) }
    val onClickCommentReport: (Long) -> Unit = { commentId ->
        reportCommentId = commentId
        showReportDialog = true
    }

    // search follow user
    val userResults = searchViewModel.searchFollowUserList.collectAsLazyPagingItems()
    val userSearchKeyword = remember { mutableStateOf("") }
    val mentionedMemberIds = remember { mutableStateListOf<SearchUser>() }
    LaunchedEffect(userSearchKeyword.value) {
        searchViewModel.searchFollowUser(userSearchKeyword.value)
    }
    val onClickFollowUser: (SearchUser) -> Unit = { mentionUser ->
        with(commentText.value) {
            val cursorPos = selection.start
            val start = text.lastIndexOf('@', cursorPos - 1)
            val end = text.indexOf(' ', start).let { if (it == -1) text.length else it }
            val newText = text.replaceRange(start, end, "@${mentionUser.nickname} ")
            commentText.value = TextFieldValue(
                text = newText,
                selection = TextRange(start + mentionUser.nickname.length + 2)
            )
            userSearchKeyword.value = ""
            mentionedMemberIds.add(mentionUser)
            showMentionSearchView.value = false
        }
    }

    // create comment
    val replyCommentState = remember { mutableStateOf<Pair<Long, Comment>?>(null) } // parent comment Id, reply comment
    val onClickPostComment: () -> Unit = {
        if (replyCommentState.value == null) postViewModel.requestCreatePostComment(commentText.value.text, postId.toInt(), mentionedMemberIds)
        else postViewModel.requestCreatePostCommentReply(replyCommentState.value!!, commentText.value.text, postId.toInt(), mentionedMemberIds)
    }
    val onClickCommentReply: (Pair<Long, Comment>?) -> Unit = { reply ->
        // set reply comment state
        replyCommentState.value = reply

        // show mention user name
        val replyUsername = "@${replyCommentState.value?.second?.nickname} "
        commentText.value = TextFieldValue(text = replyUsername, selection = TextRange(replyUsername.length))
        commentFocusRequester.requestFocus()
    }

    // clear comment
    val clearComment = {
        commentText.value = TextFieldValue("")
        replyCommentState.value = null
        mentionedMemberIds.clear()
    }
    val onClickCancelReply: () -> Unit = {
        clearComment()
    }
    val postCommentCreateSuccess by postViewModel.postCommentCreateSuccess.observeAsState(Event(false))
    if (postCommentCreateSuccess.getContentIfNotHandled() == true) {
        clearComment()
        postViewModel.requestPostComment(postId.toInt())
    }

    LaunchedEffect(Unit) {
        postViewModel.requestPostDetail(postId.toInt())
        postViewModel.requestPostComment(postId.toInt())
    }

    LaunchedEffect(postState.value) {
        post = when (postState.value?.status) {
            Status.SUCCESS -> postState.value?.data ?: DEFAULT_POST
            else -> DEFAULT_POST
        }
    }

    PostScreen(
        postId = postId,
        post = post,
        comments = when (commentState.value?.status) {
            Status.SUCCESS -> commentState.value?.data ?: DEFAULT_COMMENTS
            else -> DEFAULT_COMMENTS
        },
        currentMemberId = postViewModel.getCurrentUserInfo().memberId,
        snackBarHostState = snackBarHostState,
        commentText = commentText,
        replyCommentState = replyCommentState,
        userSearchKeyword = userSearchKeyword,
        showMentionSearchView = showMentionSearchView,
        userResults = userResults,
        commentFocusRequester = commentFocusRequester,
        onClickPostComment = onClickPostComment,
        onClickProfile = onProfileClick,
        onClickPost = { },
        onClickLikePost = {
            postViewModel.toggleLikePost(postId = postId.toInt(), currentHeart = post.heart)
        },
        onClickComment = { },
        onClickBookmark = {
            postViewModel.toggleBookmarkPostDetail(postId = postId.toInt(), currentBookmark = post.bookmark)
        },
        onClickReport = { reason ->
            reportViewModel.requestSavePostReport(reason, postId.toInt())
        },
        onPostLikeUsersClick = onPostLikeUsersClick,
        onPostHashtagClick = onPostHashtagClick,
        onClickCommentReply = onClickCommentReply,
        onClickCommentDelete = onClickCommentDelete,
        onClickCommentReport = onClickCommentReport,
        onClickFollowUser = onClickFollowUser,
        onClickCancelReply = onClickCancelReply,
        onBackClick = onBackClick
    )

    reportCommentId?.let { commentId ->
        if (showReportDialog) {
            CommentReportDialog(
                onClickClose = { showReportDialog = !showReportDialog },
                onClickConfirm = { reason ->
                    reportViewModel.requestSaveCommentReport(reason, commentId)
                    showReportDialog = !showReportDialog
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(context.getString(R.string.comment_report_message))
                    }
                }
            )
        }
    }
}

@Composable
private fun PostScreen(
    postId: String,
    post: PostDetail,
    comments: Comments,
    currentMemberId: String?,
    snackBarHostState: SnackbarHostState,
    commentText: MutableState<TextFieldValue>,
    replyCommentState: MutableState<Pair<Long, Comment>?>,
    userSearchKeyword: MutableState<String>,
    showMentionSearchView: MutableState<Boolean>,
    userResults: LazyPagingItems<SearchUser>,
    commentFocusRequester: FocusRequester,
    onClickPostComment: () -> Unit,
    onClickProfile: (String) -> Unit,
    onClickPost: () -> Unit,
    onClickLikePost: () -> Unit,
    onClickComment: () -> Unit,
    onClickBookmark: () -> Unit,
    onClickReport: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    onClickCommentReply: (Pair<Long, Comment>) -> Unit,
    onClickCommentDelete: (Long) -> Unit,
    onClickCommentReport: (Long) -> Unit,
    onClickFollowUser: (SearchUser) -> Unit,
    onClickCancelReply: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_x_sign),
                            contentDescription = stringResource(id = R.string.back_sign),
                            tint = Dark
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val coroutineScope = rememberCoroutineScope()
        val lazyListState = rememberLazyListState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    DetailPostView(
                        postId = postId,
                        post = post,
                        commentCount = comments.count,
                        currentMemberId = currentMemberId,
                        snackBarHostState = snackBarHostState,
                        onClickProfile = onClickProfile,
                        onClickPost = onClickPost,
                        onClickLikePost = onClickLikePost,
                        onClickComment = {
                            coroutineScope.launch {
                                commentFocusRequester.requestFocus()
                                lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
                            }
                        },
                        onClickBookmark = onClickBookmark,
                        onClickReport = onClickReport,
                        onPostLikeUsersClick = onPostLikeUsersClick,
                        onPostHashtagClick = onPostHashtagClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.padding(horizontal = 18.dp)) {
                        val dec = DecimalFormat("#,###")
                        Text(text = " ${dec.format(comments.count)} ", style = DayoTheme.typography.caption1, color = if (comments.count != 0) Primary_23C882 else Gray4_C5CAD2)
                        Text(text = stringResource(id = R.string.post_comment_count_message), style = DayoTheme.typography.caption1.copy(Gray2_767B83))
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                    CommentListView(
                        postComments = comments,
                        onClickReply = onClickCommentReply,
                        onClickDelete = onClickCommentDelete,
                        onClickReport = onClickCommentReport,
                        currentMemberId = currentMemberId,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
            }
            if (showMentionSearchView.value) CommentMentionSearchView(userResults, onClickFollowUser)
            if (replyCommentState.value != null) CommentReplyDescriptionView(replyCommentState, onClickCancelReply)
            CommentTextField(commentText, replyCommentState, userSearchKeyword, showMentionSearchView, commentFocusRequester, onClickPostComment)
        }
    }
}

@Preview
@Composable
private fun PreviewPostScreen() {
    val commentText = remember { mutableStateOf(TextFieldValue("")) }
    val replyCommentState = remember { mutableStateOf<Pair<Long, Comment>?>(null) } // parent comment Id, reply comment
    val userSearchKeyword = remember { mutableStateOf("") }
    val showMentionSearchView = remember { mutableStateOf(false) }
    val userResults = flowOf(PagingData.empty<SearchUser>()).collectAsLazyPagingItems()

    DayoTheme {
        PostScreen(
            postId = "0",
            post = DEFAULT_POST,
            comments = DEFAULT_COMMENTS,
            currentMemberId = "",
            snackBarHostState = SnackbarHostState(),
            commentText = commentText,
            replyCommentState = replyCommentState,
            userSearchKeyword = userSearchKeyword,
            showMentionSearchView = showMentionSearchView,
            userResults = userResults,
            commentFocusRequester = FocusRequester(),
            onClickPostComment = { },
            onClickProfile = { },
            onClickPost = { },
            onClickLikePost = { },
            onClickComment = { },
            onClickBookmark = { },
            onClickReport = { },
            onPostLikeUsersClick = { },
            onPostHashtagClick = { },
            onClickCommentReply = { },
            onClickCommentDelete = { },
            onClickCommentReport = { },
            onClickFollowUser = { },
            onClickCancelReply = { },
            onBackClick = { }
        )
    }
}

