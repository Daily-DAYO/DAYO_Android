package daily.dayo.presentation.view

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.TextFieldDecorationBox
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.paging.compose.LazyPagingItems
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.MentionUser
import daily.dayo.domain.model.SearchUser
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.TimeChangerUtil
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF

@Composable
fun CommentListView(
    currentMemberId: String?,
    postComments: Comments,
    onClickProfile: (String) -> Unit,
    onClickReply: (Pair<Long, Comment>) -> Unit,
    onClickDelete: (Long) -> Unit,
    onClickReport: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showEmptyIcon: Boolean = false
) {
    with(postComments) {
        data.let { postComments ->
            if (postComments.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(DayoTheme.colorScheme.background)
                        .fillMaxSize()
                        .padding(top = 12.dp, bottom = 30.dp)
                        .then(modifier)
                ) {
                    if (showEmptyIcon) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment_empty),
                            contentDescription = "empty",
                            tint = Color.Unspecified
                        )
                    }

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
                Column(
                    modifier = Modifier
                        .background(DayoTheme.colorScheme.background)
                        .fillMaxSize()
                        .then(modifier)
                ) {
                    postComments.forEach { comment ->
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
                                onClickProfile = onClickProfile,
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
                                    onClickProfile = onClickProfile,
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
}

@Composable
fun CommentView(
    parentCommentId: Long,
    comment: Comment,
    isMine: Boolean,
    onClickProfile: (String) -> Unit,
    onClickReply: (Pair<Long, Comment>) -> Unit,
    onClickDelete: (Long) -> Unit,
    onClickReport: (Long) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoundImageView(
                imageUrl = "${BuildConfig.BASE_URL}/images/${comment.profileImg}",
                context = LocalContext.current,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(36.dp)
                    .clickableSingle(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onClickProfile(comment.memberId) }
                    ),
                imageDescription = "comment profile image"
            )
            Column {
                Row(
                    modifier = Modifier.padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // comment nickname
                    Text(
                        text = comment.nickname,
                        style = DayoTheme.typography.caption3.copy(Gray1_50545B),
                        modifier = Modifier
                            .clickableSingle(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onClickProfile(comment.memberId) }
                            )
                    )
                    Spacer(Modifier.width(4.dp))

                    // comment create time
                    Text(
                        text = TimeChangerUtil.timeChange(context = LocalContext.current, time = comment.createTime),
                        style = DayoTheme.typography.caption6.copy(Gray4_C5CAD2)
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
                            indication = ripple(bounded = false, radius = 8.dp),
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
                            style = DayoTheme.typography.caption4.copy(Gray3_9FA5AE),
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Text(
                        text = "•",
                        style = DayoTheme.typography.caption4.copy(Gray3_9FA5AE)
                    )
                    Spacer(Modifier.width(8.dp))

                    // comment option
                    Text(
                        text = if (isMine) "삭제" else "신고",
                        style = DayoTheme.typography.caption4.copy(Gray3_9FA5AE),
                        modifier = Modifier
                            .clickableSingle(
                                indication = ripple(bounded = false, radius = 8.dp),
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
fun CommentMentionSearchView(userResults: LazyPagingItems<SearchUser>, onClickFollowUser: (SearchUser) -> Unit) {
    val placeholder = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_profile_default_user_profile)
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
                            indication = ripple(bounded = false, radius = 8.dp, color = Gray7_F6F6F7),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onClickFollowUser(user) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundImageView(
                        imageUrl = "${BuildConfig.BASE_URL}/images/${user.profileImg}",
                        context = LocalContext.current,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(24.dp)
                            .clickableSingle(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { }
                            ),
                        imageDescription = "search users profile image",
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = user.nickname)
                }
            }
        }
    }
}

@Composable
fun CommentReplyDescriptionView(replyCommentState: MutableState<Pair<Long, Comment>?>, onClickCancelReply: () -> Unit) {
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
                style = DayoTheme.typography.caption4.copy(Gray1_50545B)
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
fun CommentTextField(
    commentText: MutableState<TextFieldValue>,
    replyCommentState: MutableState<Pair<Long, Comment>?>,
    userSearchKeyword: MutableState<String>,
    showMentionSearchView: MutableState<Boolean>,
    focusRequester: FocusRequester,
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
                .weight(1f)
                .focusRequester(focusRequester),
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
                    shape = DayoTheme.shapes.small.copy(all = CornerSize(12.dp)),
                    colors = textFieldColors(backgroundColor = Gray7_F6F6F7),
                    contentPadding = TextFieldDefaults.textFieldWithLabelPadding(top = 8.dp, bottom = 8.dp, start = 12.dp)
                )
            }
        )

        Text(
            text = stringResource(R.string.comment_button_text),
            textAlign = TextAlign.Center,
            style = DayoTheme.typography.b5.copy(color = White_FFFFFF, fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .defaultMinSize(minWidth = 64.dp, minHeight = 36.dp)
                .background(Primary_23C882, shape = RoundedCornerShape(12.dp))
                .clickableSingle { onClickPostComment() }
                .padding(vertical = 8.dp, horizontal = 12.dp)
        )
    }
}

// Preview
@Preview
@Composable
private fun PreviewCommentView() {
    CommentView(
        parentCommentId = 0,
        comment = Comment(0, "", "닉네임", "", "댓글", "2024-07-20T00:58:45.162925", emptyList(), emptyList()),
        onClickProfile = {},
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

@Preview
@Composable
private fun PreviewCommentTextField() {
    val commentText = remember { mutableStateOf(TextFieldValue("")) }
    val replyCommentState = remember { mutableStateOf<Pair<Long, Comment>?>(null) } // parent comment Id, reply comment
    val userSearchKeyword = remember { mutableStateOf("") }
    val showMentionSearchView = remember { mutableStateOf(false) }
    val commentFocusRequester = FocusRequester()
    CommentTextField(
        commentText, replyCommentState, userSearchKeyword, showMentionSearchView, commentFocusRequester, { }
    )
}