package daily.dayo.presentation.screen.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.Post
import daily.dayo.domain.model.categoryKR
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.TimeChangerUtil
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.Transparent_White30
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.HashtagHorizontalGroup
import daily.dayo.presentation.view.OthersPostDropdownMenu
import daily.dayo.presentation.view.dialog.RadioButtonDialog
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PostScreen(
    postId: String,
    snackBarHostState: SnackbarHostState,
    reportViewModel: ReportViewModel = hiltViewModel(),
    postViewModel: PostViewModel = hiltViewModel()
) {
    
    PostView(
        post = DEFAULT_POST,
        snackBarHostState = snackBarHostState,
        onClickProfile = { /*TODO*/ },
        onClickPost = { /*TODO*/ },
        onClickLikePost = { /*TODO*/ },
        onClickBookmark = { /*TODO*/ },
        onClickReport = { reason ->
            reportViewModel.requestSavePostReport(reason, postId.toInt())
        },
        onPostLikeUsersClick = {

        },
        onPostHashtagClick = {

        }
    )
}

@Composable
private fun PostView(
    post: Post,
    snackBarHostState: SnackbarHostState,
    onClickProfile: () -> Unit,
    onClickPost: () -> Unit,
    onClickLikePost: () -> Unit,
    onClickBookmark: () -> Unit,
    onClickReport: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageInteractionSource = remember { MutableInteractionSource() }
    var showPostOption by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val reportReasons = arrayListOf(
        stringResource(id = R.string.report_post_reason_1),
        stringResource(id = R.string.report_post_reason_2),
        stringResource(id = R.string.report_post_reason_3),
        stringResource(id = R.string.report_post_reason_4),
        stringResource(id = R.string.report_post_reason_5),
        stringResource(id = R.string.report_post_reason_6),
        stringResource(id = R.string.report_post_reason_7),
        stringResource(id = R.string.report_post_reason_other),
    )

    Column(modifier = modifier) {
        // publisher info
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // user profile image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${BuildConfig.BASE_URL}/images/${post.userProfileImage}")
                    .build(),
                contentDescription = "${post.nickname} profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(shape = CircleShape)
                    .align(Alignment.CenterVertically)
                    .clickableSingle(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClickProfile
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            // post info text
            Column(Modifier.weight(1f)) {
                Text(
                    text = post.nickname,
                    style = DayoTheme.typography.b5.copy(Dark),
                    modifier = Modifier.clickableSingle(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClickProfile
                    )
                )
                Text(
                    text = categoryKR(post.category ?: Category.ETC) + " ･ " +
                            post.createDateTime?.let { TimeChangerUtil.timeChange(context = LocalContext.current, time = it) },
                    style = DayoTheme.typography.caption3.copy(Gray3_9FA5AE)
                )
            }
            // post option
            Box {
                IconButton(
                    onClick = { showPostOption = true }
                ) {
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        painter = painterResource(id = R.drawable.ic_option_horizontal),
                        tint = Gray2_767B83,
                        contentDescription = "post option"
                    )
                }
                OthersPostDropdownMenu(
                    expanded = showPostOption,
                    onDismissRequest = { showPostOption = !showPostOption },
                    onPostReportClick = {
                        showDialog = !showDialog
                        showPostOption = !showPostOption
                    }
                )
            }
        }

        // post images
        Box(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .padding(horizontal = 18.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            post.postImages?.let { postImages ->
                val pagerState = rememberPagerState(pageCount = { postImages.size })
                HorizontalPager(state = pagerState) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("${BuildConfig.BASE_URL}/images/${postImages[it]}")
                            .build(),
                        contentDescription = "post images",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(size = 16.dp))
                            .clickableSingle(
                                interactionSource = imageInteractionSource,
                                indication = null,
                                onClick = onClickPost
                            )
                    )
                }

                // indicator
                if (postImages.size > 1) {
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 4.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color = if (pagerState.currentPage == iteration) White_FFFFFF else Transparent_White30
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(color)
                                    .width(if (pagerState.currentPage == iteration) 12.dp else 6.dp)
                                    .height(6.dp)
                            )
                        }
                    }
                }
            }
        }

        // reaction
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // like button
            Image(
                imageVector = ImageVector.vectorResource(id = if (post.heart) R.drawable.ic_heart_filled else R.drawable.ic_heart_outlined),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .padding(vertical = 8.dp)
                    .clickableSingle(
                        indication = rememberRipple(bounded = false),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClickLikePost
                    ),
                contentDescription = "like",
            )

            // comment
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_comment),
                modifier = Modifier
                    .padding(8.dp)
                    .clickableSingle(
                        indication = rememberRipple(bounded = false),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { }
                    ),
                contentDescription = "comment",
            )
            Spacer(modifier = Modifier.weight(1f))

            // bookmark
            Image(
                imageVector = ImageVector.vectorResource(id = if (post.bookmark == true) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_default),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickableSingle(
                        indication = rememberRipple(bounded = false),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClickBookmark
                    ),
                contentDescription = "bookmark",
            )
        }

        // post info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .padding(horizontal = 18.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // like count
            val dec = DecimalFormat("#,###")
            Row(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.post_like_count_message_1), style = DayoTheme.typography.caption1.copy(Gray2_767B83))
                Text(text = " ${dec.format(post.heartCount)} ",
                    style = DayoTheme.typography.caption1,
                    modifier = if (post.heartCount != 0) Modifier.clickableSingle { onPostLikeUsersClick(post.postId.toString()) } else Modifier,
                    color = if (post.heartCount != 0) Primary_23C882 else Gray4_C5CAD2)
                Text(text = stringResource(id = R.string.post_like_count_message_2), style = DayoTheme.typography.caption1.copy(Gray2_767B83))
            }

            // comment count
            Row {
                Text(text = " ${dec.format(post.commentCount)} ", style = DayoTheme.typography.caption1, color = if (post.commentCount != 0) Primary_23C882 else Gray4_C5CAD2)
                Text(text = stringResource(id = R.string.post_comment_count_message), style = DayoTheme.typography.caption1.copy(Gray2_767B83))
            }
        }

        // post content
        if (post.contents.isNullOrEmpty().not()) {
            Text(
                text = post.contents!!,
                style = DayoTheme.typography.b6.copy(Dark)
            )
        }

        // hashtags
        if (post.hashtags.isNullOrEmpty().not()) {
            HashtagHorizontalGroup(
                hashtags = post.hashtags!!,
                onPostHashtagClick = onPostHashtagClick
            )
        }
    }

    if (showDialog) {
        RadioButtonDialog(
            title = stringResource(id = R.string.report_post_title),
            description = stringResource(id = R.string.report_post_description),
            radioItems = reportReasons,
            lastInputEnabled = true,
            lastTextPlaceholder = "게시물을 신고하는 기타 사유는 무엇인가요?",
            lastTextMaxLength = 100,
            onClickCancel = { showDialog = !showDialog },
            onClickConfirm = { reason ->
                onClickReport(reason)
                showDialog = !showDialog
                coroutineScope.launch {
                    snackBarHostState.showSnackbar("신고가 접수되었어요.")
                }
            },
            modifier = Modifier
                .height(400.dp)
                .imePadding()
                .clip(RoundedCornerShape(28.dp))
                .background(DayoTheme.colorScheme.background)
        )
    }
}

@Preview
@Composable
private fun PreviewPostScreen() {
    DayoTheme {
        PostView(
            post = DEFAULT_POST,
            snackBarHostState = SnackbarHostState(),
            onClickProfile = { /*TODO*/ },
            onClickPost = { /*TODO*/ },
            onClickLikePost = { /*TODO*/ },
            onClickBookmark = { /*TODO*/ },
            onClickReport = {},
            onPostLikeUsersClick = {},
            onPostHashtagClick = {}
        )
    }
}

private val DEFAULT_POST = Post(
    postId = 0,
    memberId = "",
    nickname = "",
    userProfileImage = "",
    category = Category.ALL,
    thumbnailImage = "",
    postImages = emptyList(),
    contents = "",
    createDateTime = LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    ),
    commentCount = 0,
    comments = emptyList(),
    hashtags = emptyList(),
    bookmark = false,
    heart = false,
    heartCount = 0,
    folderId = 0,
    folderName = "",
    preLoadThumbnail = null,
    preLoadUserImg = null
)
