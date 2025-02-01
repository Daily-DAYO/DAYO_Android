package daily.dayo.presentation.view.dialog

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.TextFieldDecorationBox
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.MentionUser
import daily.dayo.domain.model.SearchUser
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.TimeChangerUtil
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.ReportViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentBottomSheetDialog(
    sheetState: ModalBottomSheetState,
    onClickClose: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    postId: Int,
    snackBarHostState: SnackbarHostState,
    postViewModel: PostViewModel,
    accountViewModel: AccountViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val currentMemberId = accountViewModel.getCurrentUserInfo().memberId
    val scrollState = rememberScrollState()
    val commentText = remember { mutableStateOf(TextFieldValue("")) }
    val showMentionSearchView = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // show comments
    val postComments by postViewModel.postComment.observeAsState(initial = Resource.loading(emptyList()))
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
                    CommentBottomSheetDialogTitle(onClickClose)
                    CommentBottomSheetDialogContent(postComments, onClickReply, onClickDelete, onClickReport, currentMemberId, scrollState)
                    if (showMentionSearchView.value) CommentMentionSearchView(userResults, onClickFollowUser)
                    if (replyCommentState.value != null) CommentReplyDescriptionView(replyCommentState, onClickCancelReply)
                    CommentTextField(commentText, replyCommentState, userSearchKeyword, showMentionSearchView, onClickPostComment)
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
private fun CommentBottomSheetDialogTitle(onClickClose: () -> Unit) {
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
            onClick = onClickClose,
            iconContentDescription = "close",
            iconPainter = painterResource(id = R.drawable.ic_x_sign),
            iconButtonModifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun CommentBottomSheetDialogContent(
    postComments: Resource<List<Comment>>,
    onClickReply: (Pair<Long, Comment>) -> Unit,
    onClickDelete: (Long) -> Unit,
    onClickReport: (Long) -> Unit,
    currentMemberId: String?,
    scrollState: ScrollState
) {
    with(postComments) {
        when (status) {
            Status.SUCCESS -> {
                data?.let { postComments ->
                    if (postComments.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .background(DayoTheme.colorScheme.background)
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_comment_empty),
                                contentDescription = "empty",
                                tint = Color.Unspecified
                            )
                            Text(
                                text = stringResource(id = R.string.post_comment_empty),
                                style = DayoTheme.typography.b3.copy(Gray3_9FA5AE),
                                modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.post_comment_empty_description),
                                style = DayoTheme.typography.caption1.copy(Gray4_C5CAD2)
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 18.dp),
                            modifier = Modifier
                                .background(DayoTheme.colorScheme.background)
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                        ) {
                            items(postComments) { comment ->
                                Column(
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .background(color = DayoTheme.colorScheme.background, shape = RoundedCornerShape(20.dp))
                                        .border(width = 1.dp, color = Gray7_F6F6F7, shape = RoundedCornerShape(20.dp))
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // comment
                                    CommentView(
                                        parentCommentId = comment.commentId,
                                        comment = comment,
                                        isMine = currentMemberId == comment.memberId,
                                        onClickReply = onClickReply,
                                        onClickDelete = onClickDelete,
                                        onClickReport = onClickReport,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                    )
                                    // reply
                                    comment.replyList.forEach { reply ->
                                        CommentView(
                                            parentCommentId = comment.commentId,
                                            comment = reply,
                                            isMine = currentMemberId == reply.memberId,
                                            onClickReply = onClickReply,
                                            onClickDelete = onClickDelete,
                                            onClickReport = onClickReport,
                                            modifier = Modifier
                                                .background(color = Gray7_F6F6F7, shape = RoundedCornerShape(20.dp))
                                                .padding(12.dp)
                                                .fillMaxWidth(0.9f)
                                                .wrapContentHeight()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Status.LOADING -> {}
            Status.ERROR -> {}
        }
    }
}

fun getAnnotatedCommentContent(content: String, mentionList: List<MentionUser>): AnnotatedString = buildAnnotatedString {
    var currentIndex = 0
    val regex = "@\\w+".toRegex()

    regex.findAll(content).forEach { matchResult ->
        val start = matchResult.range.first
        val end = matchResult.range.last + 1
        val matchedText = matchResult.value

        // 일반 텍스트 추가
        append(content.substring(currentIndex, start))

        // 매칭된 @유저명
        if (mentionList.any { it.nickname == matchedText.substring(1) }) {
            withStyle(style = SpanStyle(color = Primary_23C882)) {
                append(matchedText)
            }
        } else {
            append(matchedText)
        }
        currentIndex = end
    }

    // 남은 일반 텍스트 추가
    if (currentIndex < content.length) {
        append(content.substring(currentIndex))
    }
}

@Composable
private fun CommentView(
    parentCommentId: Long,
    comment: Comment,
    isMine: Boolean,
    onClickReply: (Pair<Long, Comment>) -> Unit,
    onClickDelete: (Long) -> Unit,
    onClickReport: (Long) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val placeholderResId = remember { R.drawable.ic_profile_default_user_profile }
            RoundImageView(context = LocalContext.current,
                imageUrl = "${BuildConfig.BASE_URL}/images/${comment.profileImg}",
                imageDescription = "comment profile image",
                placeholderResId = placeholderResId,
                customModifier = Modifier
                    .clip(CircleShape)
                    .size(36.dp)
                    .clickableSingle(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { } // todo onClickProfile
                    )
            )
            Column {
                Row(
                    modifier = Modifier.padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // comment nickname
                    Text(text = comment.nickname,
                        style = DayoTheme.typography.caption2.copy(Dark),
                        modifier = Modifier
                            .clickableSingle(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { } // todo onClickProfile
                            )
                    )
                    Spacer(Modifier.width(4.dp))

                    // comment create time
                    Text(
                        text = TimeChangerUtil.timeChange(context = LocalContext.current, time = comment.createTime),
                        style = DayoTheme.typography.caption5.copy(Gray4_C5CAD2)
                    )
                }

                // comment content
                Spacer(Modifier.height(2.dp))
                Text(
                    text = getAnnotatedCommentContent(comment.contents, comment.mentionList),
                    style = DayoTheme.typography.b6.copy(Dark)
                )
                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // reply comment
                    Row(
                        modifier = Modifier.clickableSingle(
                            indication = rememberRipple(bounded = false, radius = 8.dp),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onClickReply(Pair(parentCommentId, comment)) }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_comment),
                            colorFilter = ColorFilter.tint(Gray3_9FA5AE),
                            modifier = Modifier.size(12.dp),
                            contentDescription = "reply comment",
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "답글쓰기",
                            style = DayoTheme.typography.b6.copy(Gray3_9FA5AE),
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Text(
                        text = "•",
                        style = DayoTheme.typography.b6.copy(Gray3_9FA5AE)
                    )
                    Spacer(Modifier.width(8.dp))

                    // comment option
                    Text(
                        text = if (isMine) "삭제" else "신고",
                        style = DayoTheme.typography.b6.copy(Gray3_9FA5AE),
                        modifier = Modifier
                            .clickableSingle(
                                indication = rememberRipple(bounded = false, radius = 8.dp),
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = if (isMine) {
                                    { onClickDelete(comment.commentId) }
                                } else {
                                    { onClickReport(comment.commentId) }
                                }
                            ),
                    )
                }
            }
        }
    }

}

@Composable
private fun CommentMentionSearchView(
    userResults: LazyPagingItems<SearchUser>,
    onClickFollowUser: (SearchUser) -> Unit
) {
    val placeholderResId = R.drawable.ic_profile_default_user_profile
    LazyColumn(
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        items(userResults.itemCount) { index ->
            userResults[index]?.let { user ->
                Row(
                    modifier = Modifier
                        .background(DayoTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickableSingle(
                            indication = rememberRipple(bounded = false, radius = 8.dp, color = Gray7_F6F6F7),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onClickFollowUser(user) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundImageView(context = LocalContext.current,
                        imageUrl = "${BuildConfig.BASE_URL}/images/${user.profileImg}",
                        imageDescription = "search users profile image",
                        placeholderResId = placeholderResId,
                        customModifier = Modifier
                            .clip(CircleShape)
                            .size(24.dp)
                            .clickableSingle(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { }
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = user.nickname)
                }
            }
        }
    }
}

@Composable
private fun CommentReplyDescriptionView(replyCommentState: MutableState<Pair<Long, Comment>?>, onClickCancelReply: () -> Unit) {
    replyCommentState.value?.let { replyComment ->
        Row(
            modifier = Modifier
                .background(color = Color(0xFFE8EAEE))
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 18.dp)
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = replyComment.second.nickname,
                style = DayoTheme.typography.caption4.copy(Dark)
            )
            Text(
                text = "님에게 답글 남기는 중",
                style = DayoTheme.typography.caption4.copy(Color(0xFF50545B))
            )
            Spacer(modifier = Modifier.weight(1f))
            DayoTextButton(
                onClick = onClickCancelReply,
                text = "취소",
                textStyle = DayoTheme.typography.caption4.copy(Gray2_767B83)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CommentTextField(
    commentText: MutableState<TextFieldValue>,
    replyCommentState: MutableState<Pair<Long, Comment>?>,
    userSearchKeyword: MutableState<String>,
    showMentionSearchView: MutableState<Boolean>,
    onClickPostComment: () -> Unit
) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Gray6_F0F1F3)
    )
    Row(
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 18.dp)
            .padding(top = 12.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
        BasicTextField(
            value = commentText.value,
            onValueChange = { inputText ->
                if (replyCommentState.value != null) {
                    val replyUsername = "@${replyCommentState.value?.second?.nickname} "
                    if (inputText.selection.start < replyUsername.length) {
                        commentText.value = commentText.value.copy(
                            selection = TextRange(replyUsername.length)
                        )
                    } else {
                        commentText.value = inputText
                    }
                } else {
                    commentText.value = inputText
                }

                val cursorPos = commentText.value.selection.start
                val text = inputText.text

                // 언급할 사용자 검색
                if (cursorPos > 0 && text.getOrNull(cursorPos - 1) == '@') {
                    showMentionSearchView.value = true
                    userSearchKeyword.value = ""
                } else {
                    // 커서 위치가 @와 공백 사이에 있을 경우에만 검색 키워드를 업데이트
                    val start = text.lastIndexOf('@', cursorPos - 1)
                    if (start >= 0) {
                        val end = text.indexOf(' ', start).let { if (it == -1) text.length else it }
                        if (cursorPos <= end) {
                            userSearchKeyword.value = text.substring(start + 1, cursorPos)
                            showMentionSearchView.value = userSearchKeyword.value.isNotEmpty()
                        } else {
                            showMentionSearchView.value = false
                        }
                    } else {
                        showMentionSearchView.value = false
                    }
                }
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .heightIn(min = 36.dp)
                .background(
                    color = Gray7_F6F6F7,
                    shape = RoundedCornerShape(12.dp)
                )
                .weight(1f),
            textStyle = DayoTheme.typography.b6,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(Primary_23C882),
            decorationBox = @Composable { innerTextField ->
                TextFieldDecorationBox(
                    value = commentText.value.text,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = { Text(text = "댓글을 남겨주세요", style = DayoTheme.typography.b6.copy(Gray4_C5CAD2)) },
                    contentPadding = TextFieldDefaults.textFieldWithLabelPadding(top = 8.dp, bottom = 8.dp, start = 12.dp)
                )
            }
        )

        Button(
            onClick = { onClickPostComment() },
            colors = ButtonDefaults.buttonColors(containerColor = Primary_23C882),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .wrapContentWidth()
                .height(36.dp),
            content = { Text(text = "남기기", style = DayoTheme.typography.b5.copy(color = White_FFFFFF, fontWeight = FontWeight.SemiBold)) },
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
        )
    }
}

@Composable
private fun CommentReportDialog(onClickClose: () -> Unit, onClickConfirm: (String) -> Unit) {
    val reportReasons = arrayListOf(
        stringResource(id = R.string.report_comment_reason_1),
        stringResource(id = R.string.report_comment_reason_2),
        stringResource(id = R.string.report_comment_reason_3),
        stringResource(id = R.string.report_comment_reason_4),
        stringResource(id = R.string.report_comment_reason_5),
        stringResource(id = R.string.report_comment_reason_6),
        stringResource(id = R.string.report_comment_reason_7)
    )

    RadioButtonDialog(
        title = stringResource(id = R.string.report_comment_title),
        description = stringResource(id = R.string.report_comment_description),
        radioItems = reportReasons,
        onClickCancel = onClickClose,
        onClickConfirm = onClickConfirm,
        modifier = Modifier
            .height(400.dp)
            .imePadding()
            .clip(RoundedCornerShape(28.dp))
            .background(DayoTheme.colorScheme.background)
    )
}

// Preview
@Preview
@Composable
private fun PreviewCommentBottomSheetDialogTitle() {
    CommentBottomSheetDialogTitle({})
}

@Preview
@Composable
private fun PreviewCommentBottomSheetDialogContent() {
    CommentBottomSheetDialogContent(Resource.success(emptyList()), {}, {}, {}, "", rememberScrollState())
}

@Preview
@Composable
private fun PreviewCommentView() {
    CommentView(
        parentCommentId = 0,
        comment = Comment(0, "", "닉네임", "", "댓글", "2024-07-20T00:58:45.162925", emptyList(), emptyList()),
        onClickReply = {},
        onClickReport = {},
        onClickDelete = {},
        isMine = true,
        modifier = Modifier
            .padding(bottom = 12.dp)
            .background(color = DayoTheme.colorScheme.background, shape = RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = Gray7_F6F6F7, shape = RoundedCornerShape(20.dp))
            .padding(12.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    )
}