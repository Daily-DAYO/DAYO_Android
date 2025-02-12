package daily.dayo.presentation.screen.post

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.PostDetail
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.view.DEFAULT_POST
import daily.dayo.presentation.view.DetailPostView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.ReportViewModel

@Composable
fun PostScreen(
    postId: String,
    snackBarHostState: SnackbarHostState,
    onProfileClick: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    onBackClick: () -> Unit,
    reportViewModel: ReportViewModel = hiltViewModel(),
    postViewModel: PostViewModel = hiltViewModel()
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
        comment = when (commentState.value?.status) {
            Status.SUCCESS -> commentState.value?.data ?: DEFAULT_COMMENT
            else -> DEFAULT_COMMENT
        },
        isMine = postViewModel.getCurrentUserInfo().memberId == post.memberId,
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
    comment: Comments,
    isMine: Boolean,
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
    val scrollState = rememberScrollState()

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

        DetailPostView(
            postId = postId,
            post = post,
            comment = comment,
            isMine = isMine,
            snackBarHostState = snackBarHostState,
            onClickProfile = onClickProfile,
            onClickPost = onClickPost,
            onClickLikePost = onClickLikePost,
            onClickBookmark = onClickBookmark,
            onClickReport = onClickReport,
            onPostLikeUsersClick = onPostLikeUsersClick,
            onPostHashtagClick = onPostHashtagClick,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        )
    }
}

@Preview
@Composable
private fun PreviewPostScreen() {
    DayoTheme {
        PostScreen(
            postId = "0",
            post = DEFAULT_POST,
            comment = DEFAULT_COMMENT,
            isMine = true,
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

val DEFAULT_COMMENT = Comments(
    count = 0,
    data = emptyList()
)
