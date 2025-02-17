package daily.dayo.presentation.screen.post

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.PostDetail
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.CommentListView
import daily.dayo.presentation.view.DEFAULT_POST
import daily.dayo.presentation.view.DetailPostView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.DEFAULT_COMMENTS
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.ReportViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel
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
    accountViewModel: AccountViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val postState = postViewModel.postDetail.observeAsState()
    var post by remember { mutableStateOf(DEFAULT_POST) }
    val commentState = postViewModel.postComments.observeAsState()

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
        onClickProfile = onProfileClick,
        onClickPost = { },
        onClickLikePost = {
            postViewModel.toggleLikePost(postId = postId.toInt(), currentHeart = post.heart)
        },
        onClickBookmark = {
            postViewModel.toggleBookmarkPostDetail(postId = postId.toInt(), currentBookmark = post.bookmark)
        },
        onClickReport = { reason ->
            reportViewModel.requestSavePostReport(reason, postId.toInt())
        },
        onPostLikeUsersClick = onPostLikeUsersClick,
        onPostHashtagClick = onPostHashtagClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun PostScreen(
    postId: String,
    post: PostDetail,
    comments: Comments,
    currentMemberId: String?,
    snackBarHostState: SnackbarHostState,
    onClickProfile: (String) -> Unit,
    onClickPost: () -> Unit,
    onClickLikePost: () -> Unit,
    onClickBookmark: () -> Unit,
    onClickReport: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
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
                    onClickReply = {},
                    onClickDelete = {},
                    onClickReport = {},
                    currentMemberId = currentMemberId,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPostScreen() {
    DayoTheme {
        PostScreen(
            postId = "0",
            post = DEFAULT_POST,
            comments = DEFAULT_COMMENTS,
            currentMemberId = "",
            snackBarHostState = SnackbarHostState(),
            onClickProfile = { },
            onClickPost = { },
            onClickLikePost = { },
            onClickBookmark = { },
            onClickReport = { },
            onPostLikeUsersClick = { },
            onPostHashtagClick = { },
            onBackClick = { }
        )
    }
}

