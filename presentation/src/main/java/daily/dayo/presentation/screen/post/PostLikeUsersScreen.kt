package daily.dayo.presentation.screen.post

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.LikeUser
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.theme.caption1
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.PostViewModel
import java.text.DecimalFormat

@Composable
fun PostLikeUsersScreen(
    likeCount: Int,
    onClickProfile: (String) -> Unit,
    onClickFollow: (LikeUser) -> Unit,
    postViewModel: PostViewModel = hiltViewModel()
) {
    val likeUserList = postViewModel.postLikeUsers.collectAsLazyPagingItems() // TODO 수정

    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_sign),
                        contentDescription = "back sign",
                        tint = Gray1_313131,
                        modifier = Modifier.padding(12.dp)
                    )
                },
                title = stringResource(id = R.string.like),
                titleAlignment = TopNavigationAlign.CENTER
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            // like count
            item {
                val dec = DecimalFormat("#,###")
                Text(text = " ${dec.format(likeCount)} ", style = MaterialTheme.typography.caption1, color = PrimaryGreen_23C882)
                Text(text = stringResource(id = R.string.post_like_count_message_2), style = MaterialTheme.typography.caption1.copy(Gray2_767B83))
            }

            // users
            items(
                count = likeUserList.itemCount,
                key = likeUserList.itemKey()
            ) { index ->
                likeUserList[index]?.let { user ->
                    LikeUserItem(likeUser = user, onClickProfile = onClickProfile, onClickFollow = onClickFollow)
                }
            }
        }
    }
}

@Composable
private fun LikeUserItem(
    likeUser: LikeUser,
    onClickProfile: (String) -> Unit,
    onClickFollow: (LikeUser) -> Unit
) {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .wrapContentHeight()
        ) {
            // profile
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${BuildConfig.BASE_URL}/images/${likeUser.profileImg}")
                    .build(),
                contentDescription = "${likeUser.nickname} + profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(16.dp)
                    .clip(shape = CircleShape)
                    .align(Alignment.CenterVertically)
                    .clickableSingle(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onClickProfile(likeUser.memberId) }
                    )
            )

            // nickname
            Text(text = likeUser.nickname,
                style = MaterialTheme.typography.b5.copy(Gray1_313131),
                modifier = Modifier.clickableSingle(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onClickProfile(likeUser.memberId) }
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // follow button

        }
    }
}