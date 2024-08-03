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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.SearchUser
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.TimeChangerUtil
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b1
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption1
import daily.dayo.presentation.theme.caption2
import daily.dayo.presentation.theme.caption5
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentBottomSheetDialog(
    sheetState: ModalBottomSheetState,
    onClickClose: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    postId: Int,
    postViewModel: PostViewModel,
    accountViewModel: AccountViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val currentMemberId = accountViewModel.getCurrentUserInfo().memberId
    val scrollState = rememberScrollState()
    val commentText = remember { mutableStateOf(TextFieldValue("")) }
    val showMentionSearchView = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch {
            sheetState.hide()
        }
    }

    // show comments
    val postComments by postViewModel.postComment.observeAsState(initial = Resource.loading(emptyList()))
    LaunchedEffect(Unit) {
        postViewModel.requestPostComment(postId)
    }

    // create comment
    val onClickPostComment: (String) -> Unit = { contents -> postViewModel.requestCreatePostComment(contents, postId) }
    val postCommentCreateSuccess by postViewModel.postCommentCreateSuccess.observeAsState(Event(true))
    if (postCommentCreateSuccess.getContentIfNotHandled() == true) {
        postViewModel.requestPostComment(postId)
        commentText.value = TextFieldValue("")
    }

    // search follow user
    val userResults = searchViewModel.searchFollowUserList.collectAsLazyPagingItems()
    val userSearchKeyword = remember { mutableStateOf("") }
    LaunchedEffect(userSearchKeyword.value) {
        searchViewModel.searchFollowUser(userSearchKeyword.value)
    }
    val onClickFollowUser: (String) -> Unit = { mentionUser ->
        with(commentText.value) {
            val cursorPos = selection.start
            val start = text.lastIndexOf('@', cursorPos - 1)
            val end = text.indexOf(' ', start).let { if (it == -1) text.length else it }
            val newText = text.replaceRange(start, end, "@$mentionUser ")
            commentText.value = TextFieldValue(
                text = newText,
                selection = TextRange(start + mentionUser.length + 2)
            )
            userSearchKeyword.value = ""
            showMentionSearchView.value = false
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
                        .wrapContentHeight()
                ) {
                    CommentBottomSheetDialogTitle(onClickClose)
                    CommentBottomSheetDialogContent(postComments, currentMemberId, scrollState)
                    if (showMentionSearchView.value) CommentMentionSearchView(userResults, onClickFollowUser)
                    CommentTextField(commentText, userSearchKeyword, showMentionSearchView, onClickPostComment)
                }
            }
        }
    ) {}
}

@Composable
private fun CommentBottomSheetDialogTitle(onClickClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(White_FFFFFF)
    ) {
        Text(
            text = stringResource(id = R.string.comment),
            modifier = Modifier
                .matchParentSize()
                .padding(top = 12.dp, bottom = 6.dp),
            textAlign = TextAlign.Center,
            color = Gray1_313131,
            style = MaterialTheme.typography.b1.copy(Gray1_313131)
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
                                .background(White_FFFFFF)
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
                                style = MaterialTheme.typography.b3.copy(Gray3_9FA5AE),
                                modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.post_comment_empty_description),
                                style = MaterialTheme.typography.caption1.copy(Gray4_C5CAD2)
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 18.dp),
                            modifier = Modifier
                                .background(White_FFFFFF)
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                        ) {
                            items(postComments) { comment ->
                                CommentView(comment, currentMemberId == comment.memberId)
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

@Composable
private fun CommentView(comment: Comment, isMine: Boolean) {
    Column(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .background(color = White_FFFFFF, shape = RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = Gray7_F6F6F7, shape = RoundedCornerShape(20.dp))
            .padding(12.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val placeholder = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_profile_default_user_profile)
            RoundImageView(context = LocalContext.current,
                imageUrl = "${BuildConfig.BASE_URL}/images/${comment.profileImg}",
                imageDescription = "comment profile image",
                placeholder = placeholder,
                customModifier = Modifier
                    .clip(CircleShape)
                    .size(36.dp)
                    .clickableSingle(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { }
                    )
            )
            Column {
                Row(
                    modifier = Modifier.padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // comment nickname
                    Text(text = comment.nickname,
                        style = MaterialTheme.typography.caption2.copy(Gray1_313131),
                        modifier = Modifier
                            .clickableSingle(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { }
                            )
                    )
                    Spacer(Modifier.width(4.dp))

                    // comment create time
                    Text(
                        text = TimeChangerUtil.timeChange(context = LocalContext.current, time = comment.createTime),
                        style = MaterialTheme.typography.caption5.copy(Gray4_C5CAD2)
                    )
                }

                // comment content
                Spacer(Modifier.height(2.dp))
                Text(
                    text = comment.contents,
                    style = MaterialTheme.typography.b6.copy(Gray1_313131)
                )
                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // reply comment
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_comment),
                        colorFilter = ColorFilter.tint(Gray3_9FA5AE),
                        modifier = Modifier
                            .size(12.dp)
                            .clickableSingle(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { }
                            ),
                        contentDescription = "reply comment",
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "답글쓰기",
                        style = MaterialTheme.typography.b6.copy(Gray3_9FA5AE),
                    )

                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.b6.copy(Gray3_9FA5AE)
                    )
                    Spacer(Modifier.width(8.dp))

                    // comment option
                    Text(
                        text = if (isMine) "삭제" else "신고",
                        style = MaterialTheme.typography.b6.copy(Gray3_9FA5AE)
                    )
                }
            }
        }
    }

}

@Composable
private fun CommentMentionSearchView(
    userResults: LazyPagingItems<SearchUser>,
    onClickFollowUser: (String) -> Unit
) {
    val placeholder = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_profile_default_user_profile)
    LazyColumn(
        modifier = Modifier
            .background(White_FFFFFF)
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        items(userResults.itemCount) { index ->
            userResults[index]?.let { user ->
                Row(
                    modifier = Modifier
                        .background(White_FFFFFF)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickableSingle(
                            indication = rememberRipple(bounded = false, radius = 8.dp, color = Gray7_F6F6F7),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onClickFollowUser(user.nickname) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundImageView(context = LocalContext.current,
                        imageUrl = "${BuildConfig.BASE_URL}/images/${user.profileImg}",
                        imageDescription = "search users profile image",
                        placeholder = placeholder,
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
private fun CommentTextField(
    commentText: MutableState<TextFieldValue>,
    userSearchKeyword: MutableState<String>,
    showMentionSearchView: MutableState<Boolean>,
    onClickPostComment: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .background(White_FFFFFF)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = commentText.value,
            onValueChange = { inputText ->
                commentText.value = inputText
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
                .wrapContentHeight()
                .padding(end = 8.dp)
                .padding(top = 12.dp, bottom = 16.dp),
            textStyle = MaterialTheme.typography.b6,
            placeholder = { Text(text = "댓글을 남겨주세요", style = MaterialTheme.typography.b6.copy(Gray4_C5CAD2)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Gray7_F6F6F7,
                unfocusedContainerColor = Gray7_F6F6F7,
                focusedBorderColor = White_FFFFFF,
                unfocusedBorderColor = White_FFFFFF,
                cursorColor = PrimaryGreen_23C882,
                focusedPlaceholderColor = Gray4_C5CAD2,
                unfocusedPlaceholderColor = Gray4_C5CAD2
            )
        )

        FilledRoundedCornerButton(
            onClick = { onClickPostComment(commentText.value.text) },
            label = "남기기",
            textStyle = MaterialTheme.typography.b5,
            modifier = Modifier
                .wrapContentWidth()
                .height(IntrinsicSize.Min)
                .padding(top = 12.dp, bottom = 16.dp),
            contentModifier = Modifier.wrapContentWidth()
        )
    }
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
    CommentBottomSheetDialogContent(Resource.success(emptyList()), "", rememberScrollState())
}

@Preview
@Composable
private fun PreviewCommentView() {
    CommentView(comment = Comment(0, "댓글", "2024-07-20T00:58:45.162925", "", "닉네임", ""), true)
}