package daily.dayo.presentation.view.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b1
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.theme.caption1
import daily.dayo.presentation.view.CharacterLimitOutlinedTextField
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.viewmodel.PostViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentBottomSheetDialog(
    sheetState: ModalBottomSheetState,
    onClickClose: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    postId: Int,
    postViewModel: PostViewModel
) {
    // todo 뒤로 가기 버튼 처리하기
    val scrollState = rememberScrollState()
    val commentText = remember { mutableStateOf(TextFieldValue("")) }

    // create comment
    val onClickPostComment: (String) -> Unit = { contents -> postViewModel.requestCreatePostComment(contents, postId) }
    val postCommentCreateSuccess by postViewModel.postCommentCreateSuccess.observeAsState(Event(true))
    if (postCommentCreateSuccess.peekContent()) {
        postViewModel.requestPostComment(postId)
        commentText.value = TextFieldValue("")
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
                    CommentBottomSheetDialogContent(true, scrollState)
                    CommentTextField(commentText, onClickPostComment)
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
private fun CommentBottomSheetDialogContent(isEmpty: Boolean, scrollState: ScrollState) {
    if (isEmpty) {
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
            modifier = Modifier
                .background(White_FFFFFF)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {

        }
    }
}

@Composable
private fun CommentTextField(commentText: MutableState<TextFieldValue>, onClickPostComment: (String) -> Unit) {
    Row(
        modifier = Modifier
            .background(White_FFFFFF)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CharacterLimitOutlinedTextField(
            value = commentText,
            placeholder = "댓글을 남겨주세요",
            maxLength = 150,
            singleLine = true,
            cornerSize = 12.dp,
            outlinedTextFieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Gray7_F6F6F7,
                unfocusedContainerColor = Gray7_F6F6F7,
                focusedBorderColor = White_FFFFFF,
                unfocusedBorderColor = White_FFFFFF,
                cursorColor = PrimaryGreen_23C882,
                focusedPlaceholderColor = Gray4_C5CAD2,
                unfocusedPlaceholderColor = Gray4_C5CAD2
            ),
            modifier = Modifier
                .wrapContentHeight()
                .padding(end = 8.dp)
                .padding(top = 12.dp, bottom = 16.dp)
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
    CommentBottomSheetDialogContent(isEmpty = false, rememberScrollState())
}

@Preview
@Composable
private fun PreviewCommentTextField() {
    val commentText = remember { mutableStateOf(TextFieldValue("")) }
    CommentTextField(commentText, { })
}