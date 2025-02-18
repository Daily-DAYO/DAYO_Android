package daily.dayo.presentation.view.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.SearchUser
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.view.CommentListView
import daily.dayo.presentation.view.CommentMentionSearchView
import daily.dayo.presentation.view.CommentReplyDescriptionView
import daily.dayo.presentation.view.CommentTextField
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.ReportViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentBottomSheetDialog(
    postId: Int,
    onClickClose: () -> Unit,
    sheetState: ModalBottomSheetState,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    postViewModel: PostViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val currentMemberId = accountViewModel.getCurrentUserInfo().memberId
    val commentText = remember { mutableStateOf(TextFieldValue("")) }
    val showMentionSearchView = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val commentFocusRequester = FocusRequester()

    // show comments
    val postComments = postViewModel.postComments.observeAsState()
    LaunchedEffect(postId) {
        postViewModel.requestPostComment(postId)
    }

    // comment option
    val onClickDelete: (Long) -> Unit = { commentId ->
        postViewModel.requestDeletePostComment(commentId)
    }
    val postCommentDeleteSuccess by postViewModel.postCommentDeleteSuccess.observeAsState(Event(false))
    if (postCommentDeleteSuccess.getContentIfNotHandled() == true) {
        postViewModel.requestPostComment(postId)
        SideEffect {
            coroutineScope.launch {
                snackBarHostState.showSnackbar("댓글이 삭제되었어요.")
            }
        }
    }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportCommentId by remember { mutableStateOf<Long?>(null) }
    val onClickReport: (Long) -> Unit = { commentId ->
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
        if (replyCommentState.value == null) postViewModel.requestCreatePostComment(commentText.value.text, postId, mentionedMemberIds)
        else postViewModel.requestCreatePostCommentReply(replyCommentState.value!!, commentText.value.text, postId, mentionedMemberIds)
    }
    val onClickReply: (Pair<Long, Comment>?) -> Unit = { reply ->
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
        postViewModel.requestPostComment(postId)
    }

    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch {
            clearComment()
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
        modifier = modifier,
        sheetContent = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .wrapContentHeight(),
                ) {
                    CommentBottomSheetDialogTitle(clearComment, onClickClose)
                    CommentBottomSheetDialogContent(
                        when (postComments.value?.status) {
                            Status.SUCCESS -> postComments.value?.data ?: DEFAULT_COMMENTS
                            else -> DEFAULT_COMMENTS
                        },
                        onClickReply,
                        onClickDelete,
                        onClickReport,
                        currentMemberId
                    )
                    if (showMentionSearchView.value) CommentMentionSearchView(userResults, onClickFollowUser)
                    if (replyCommentState.value != null) CommentReplyDescriptionView(replyCommentState, onClickCancelReply)
                    CommentTextField(commentText, replyCommentState, userSearchKeyword, showMentionSearchView, commentFocusRequester, onClickPostComment)
                }
            }
        }
    ) {
        reportCommentId?.let { id ->
            if (showReportDialog) {
                CommentReportDialog(
                    onClickClose = { showReportDialog = !showReportDialog },
                    onClickConfirm = {
                        reportViewModel.requestSaveCommentReport(it, id)
                        showReportDialog = !showReportDialog
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("신고가 접수되었어요.")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CommentBottomSheetDialogTitle(clearComment: () -> Unit, onClickClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(DayoTheme.colorScheme.background)
    ) {
        Text(
            text = stringResource(id = R.string.comment),
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            style = DayoTheme.typography.b1.copy(color = Dark, fontWeight = FontWeight.SemiBold)
        )

        NoRippleIconButton(
            onClick = {
                clearComment()
                onClickClose()
            },
            iconContentDescription = "close",
            iconPainter = painterResource(id = R.drawable.ic_x_sign),
            iconButtonModifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun CommentBottomSheetDialogContent(
    postComments: Comments,
    onClickReply: (Pair<Long, Comment>) -> Unit,
    onClickDelete: (Long) -> Unit,
    onClickReport: (Long) -> Unit,
    currentMemberId: String?
) {
    LazyColumn(
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        item {
            CommentListView(
                postComments,
                onClickReply,
                onClickDelete,
                onClickReport,
                currentMemberId,
                Modifier.padding(horizontal = 18.dp),
                true
            )
        }
    }
}

// Preview
@Preview
@Composable
private fun PreviewCommentBottomSheetDialogTitle() {
    CommentBottomSheetDialogTitle({}, {})
}

@Preview
@Composable
private fun PreviewCommentBottomSheetDialogContent() {
    CommentBottomSheetDialogContent(DEFAULT_COMMENTS, {}, {}, {}, "")
}

val DEFAULT_COMMENTS = Comments(
    count = 0,
    data = emptyList()
)
