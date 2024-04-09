package daily.dayo.presentation.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.Post
import daily.dayo.domain.model.categoryKR
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.TimeChangerUtil
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.White_Alpha30_FFFFFF
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption1
import daily.dayo.presentation.theme.caption3
import java.text.DecimalFormat

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedPostView(
    post: Post,
    modifier: Modifier = Modifier,
    onClickProfile: () -> Unit,
    onClickPost: () -> Unit,
    onClickLikePost: () -> Unit,
    onClickBookmark: () -> Unit
) {
    val imageInteractionSource = remember { MutableInteractionSource() }
    Column(modifier = modifier) {
        // publisher info
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${BuildConfig.BASE_URL}/images/${post.userProfileImage}")
                    .build(),
                contentDescription = "${post.nickname} + profile",
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
            Column(Modifier.weight(1f)) {
                Text(
                    text = post.nickname,
                    style = MaterialTheme.typography.b5.copy(Gray1_313131),
                    modifier = Modifier.clickableSingle(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClickProfile
                    )
                )
                Text(
                    text = categoryKR(post.category ?: Category.ETC) + " ･ " +
                            post.createDateTime?.let { TimeChangerUtil.timeChange(context = LocalContext.current, time = it) },
                    style = MaterialTheme.typography.caption3.copy(Gray3_9FA5AE)
                )
            }
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = R.drawable.ic_option_horizontal),
                tint = Gray2_767B83,
                contentDescription = "post option"
            )
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
                            val color = if (pagerState.currentPage == iteration) White_FFFFFF else White_Alpha30_FFFFFF
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
                        onClick = onClickPost
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
                Text(text = stringResource(id = R.string.post_like_count_message_1), style = MaterialTheme.typography.caption1.copy(Gray2_767B83))
                Text(text = " ${dec.format(post.heartCount)} ", style = MaterialTheme.typography.caption1, color = if (post.heartCount != 0) PrimaryGreen_23C882 else Gray4_C5CAD2)
                Text(text = stringResource(id = R.string.post_like_count_message_2), style = MaterialTheme.typography.caption1.copy(Gray2_767B83))
            }

            // comment count
            Row {
                Text(text = " ${dec.format(post.commentCount)} ", style = MaterialTheme.typography.caption1, color = if (post.commentCount != 0) PrimaryGreen_23C882 else Gray4_C5CAD2)
                Text(text = stringResource(id = R.string.post_comment_count_message), style = MaterialTheme.typography.caption1.copy(Gray2_767B83))
            }
        }

        // post content
        if (!post.contents.isNullOrEmpty()) {
            SeeMoreText(
                text = post.contents!!,
                minimizedMaxLines = 2,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 18.dp),
                onClickPost = onClickPost
            )
        }

        // hashtags
        if (!post.hashtags.isNullOrEmpty()) {
            HashtagHorizontalGroup(hashtags = post.hashtags!!)
        }
    }
}

@Composable
fun HashtagHorizontalGroup(
    hashtags: List<String>
) {
    LazyRow(contentPadding = PaddingValues(vertical = 4.dp, horizontal = 18.dp)) {
        hashtags.forEach { hashtag ->
            item {
                Row(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE4F7ED))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_hashtag),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center),
                            tint = PrimaryGreen_23C882
                        )
                    }
                    Text(
                        text = hashtag,
                        style = MaterialTheme.typography.caption1.copy(PrimaryGreen_23C882),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SeeMoreText(
    text: String,
    minimizedMaxLines: Int = 2,
    onClickPost: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    var cutText by remember(text) { mutableStateOf<String?>(null) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val seeMoreSizeState = remember { mutableStateOf<IntSize?>(null) }
    val seeMoreOffsetState = remember { mutableStateOf<Offset?>(null) }

    val textLayoutResult = textLayoutResultState.value
    val seeMoreSize = seeMoreSizeState.value
    val seeMoreOffset = seeMoreOffsetState.value

    LaunchedEffect(text, textLayoutResult, seeMoreSize) {
        val lastLineIndex = minimizedMaxLines - 1
        if (textLayoutResult != null && seeMoreSize != null
            && lastLineIndex + 1 == textLayoutResult.lineCount
            && textLayoutResult.isLineEllipsized(lastLineIndex)
        ) {
            var lastCharIndex = textLayoutResult.getLineEnd(lastLineIndex, visibleEnd = true) + 1
            var charRect: Rect
            do {
                lastCharIndex -= 1
                charRect = textLayoutResult.getCursorRect(lastCharIndex)
            } while (
                charRect.left > textLayoutResult.size.width - seeMoreSize.width
            )
            seeMoreOffsetState.value = Offset(charRect.left, charRect.bottom - seeMoreSize.height)
            cutText = text.substring(startIndex = 0, endIndex = lastCharIndex - 3) + "..."
        }
    }

    Box(modifier) {
        Text(
            text = cutText ?: text,
            style = MaterialTheme.typography.b6.copy(Gray1_313131),
            maxLines = minimizedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResultState.value = it },
        )

        val density = LocalDensity.current
        Text(
            text = " " + stringResource(id = R.string.post_contents_more),
            onTextLayout = { seeMoreSizeState.value = it.size },
            style = MaterialTheme.typography.b6.copy(Gray3_9FA5AE),
            modifier = Modifier
                .then(
                    if (seeMoreOffset != null)
                        Modifier.offset(
                            x = with(density) { seeMoreOffset.x.toDp() },
                            y = with(density) { seeMoreOffset.y.toDp() },
                        )
                    else Modifier
                )
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = { onClickPost() })
                .alpha(if (seeMoreOffset != null) 1f else 0f)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewFeedPostView() {
    MaterialTheme {
        FeedPostView(
            post = Post(
                postId = 0,
                thumbnailImage = "",
                memberId = "",
                nickname = "nickname",
                userProfileImage = "",
                heartCount = 123456,
                commentCount = 8100,
                heart = true,
                category = Category.SCHEDULER,
                postImages = null,
                contents = "힘차게 달려 신나게 어디든지 갈 수 있어",
                createDateTime = "2024-02-21T21:54:56",
                folderId = null,
                folderName = null,
                comments = null,
                hashtags = listOf("태그", "다요다요"),
                bookmark = null
            ),
            onClickPost = {},
            onClickLikePost = {},
            onClickProfile = {},
            onClickBookmark = {}
        )
    }
}
