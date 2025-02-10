package daily.dayo.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.Post
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import java.text.DecimalFormat

@Composable
fun HomePostView(
    post: Post,
    modifier: Modifier = Modifier,
    isDayoPick: Boolean = false,
    onClickPost: () -> Unit,
    onClickLikePost: () -> Unit,
    onClickNickname: () -> Unit
) {
    val imageInteractionSource = remember { MutableInteractionSource() }
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            // thumbnail image
            RoundImageView(
                context = LocalContext.current,
                imageUrl = "${BuildConfig.BASE_URL}/images/${post.thumbnailImage}",
                imageDescription = "dayo pick image",
                modifier = Modifier
                    .matchParentSize()
                    .clickableSingle(
                        interactionSource = imageInteractionSource,
                        indication = null,
                        onClick = { onClickPost() }
                    )
            )

            // dayo pick icon
            if (isDayoPick) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_dayo_pick),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp),
                    contentDescription = null
                )
            }

            // like button
            Image(
                imageVector = ImageVector.vectorResource(id = if (post.heart) R.drawable.ic_heart_filled else R.drawable.ic_heart),
                modifier = Modifier
                    .padding(bottom = 12.dp, end = 11.dp)
                    .align(Alignment.BottomEnd)
                    .clickableSingle(
                        indication = rememberRipple(bounded = false),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onClickLikePost() }
                    ),
                contentDescription = "like Button",
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // publisher info
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .wrapContentHeight()
                .clickableSingle(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onClickNickname() }
                )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${BuildConfig.BASE_URL}/images/${post.userProfileImage}")
                    .build(),
                contentDescription = "${post.nickname} + profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(16.dp)
                    .clip(shape = CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Text(text = post.nickname, style = DayoTheme.typography.b5.copy(Dark))
        }

        Spacer(modifier = Modifier.height(2.dp))

        // post info
        val dec = DecimalFormat("#,###")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(id = R.string.like) + " ${dec.format(post.heartCount)}", style = DayoTheme.typography.caption3.copy(Gray3_9FA5AE))
            Text(text = stringResource(id = R.string.comment) + " ${dec.format(post.commentCount)}", style = DayoTheme.typography.caption3.copy(Gray3_9FA5AE))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewHomePostView() {
    DayoTheme {
        HomePostView(
            modifier = Modifier.size(156.dp, 205.dp),
            post = Post(
                postId = 0,
                thumbnailImage = "",
                memberId = "",
                nickname = "nickname",
                userProfileImage = "",
                heartCount = 123456,
                commentCount = 8100,
                heart = true,
                category = null,
                postImages = null,
                contents = null,
                createDateTime = null,
                folderId = null,
                folderName = null,
                comments = null,
                hashtags = null,
                bookmark = null
            ),
            isDayoPick = true,
            onClickPost = {},
            onClickLikePost = {},
            onClickNickname = {}
        )
    }
}
