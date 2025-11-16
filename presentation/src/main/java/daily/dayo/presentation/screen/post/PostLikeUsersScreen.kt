package daily.dayo.presentation.screen.post

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.LikeUser
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoOutlinedButton
import daily.dayo.presentation.view.FilledButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.FollowViewModel
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.flowOf
import java.text.DecimalFormat

@Composable
fun PostLikeUsersScreen(
    postId: Long,
    onProfileClick: (String) -> Unit,
    onBackClick: () -> Unit,
    postViewModel: PostViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    followViewModel: FollowViewModel = hiltViewModel()
) {
    val currentMemberId = profileViewModel.currentMemberId
    val likeCount = postViewModel.postLikeCountUiState.collectAsStateWithLifecycle()
    val likeUserList = postViewModel.postLikeUsers.collectAsLazyPagingItems()
    val onFollowClick: (LikeUser) -> Unit = { user ->
        if (!user.follow) {
            followViewModel.requestFollow(
                followerId = user.memberId,
                isFollower = false
            )
        } else {
            followViewModel.requestUnfollow(
                followerId = user.memberId,
                isFollower = true
            )
        }
    }

    val followSuccess by followViewModel.followingFollowSuccess.observeAsState()
    val unFollowSuccess by followViewModel.followerUnfollowSuccess.observeAsState()

    LaunchedEffect(likeCount, followSuccess, unFollowSuccess) {
        postViewModel.requestPostDetail(postId = postId)
        postViewModel.requestPostLikeUsers(postId = postId)
    }

    if (currentMemberId != null) {
        PostLikeUsersScreen(
            currentMemberId = currentMemberId,
            likeCount = likeCount.value,
            likeUsers = likeUserList,
            onProfileClick = onProfileClick,
            onFollowClick = onFollowClick,
            onBackClick = onBackClick
        )
    } else {
        // TODO 에러 처리
    }
}

@Composable
private fun PostLikeUsersScreen(
    currentMemberId: String,
    likeCount: Int,
    likeUsers: LazyPagingItems<LikeUser>,
    onProfileClick: (String) -> Unit,
    onFollowClick: (LikeUser) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier
                            .indication(interactionSource = remember { MutableInteractionSource() }, indication = null)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left_2),
                            contentDescription = "back sign",
                            tint = Dark
                        )
                    }
                },
                title = stringResource(id = R.string.like),
                titleAlignment = TopNavigationAlign.CENTER
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .background(DayoTheme.colorScheme.background)
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp)
        ) {
            // like count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dec = DecimalFormat("#,###")
                    Text(
                        text = " ${dec.format(likeCount)} ",
                        style = DayoTheme.typography.caption1.copy(Primary_23C882),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Text(
                        text = "개의 좋아요",
                        style = DayoTheme.typography.caption1.copy(Gray2_767B83),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            // users
            items(
                count = likeUsers.itemCount,
                key = likeUsers.itemKey()
            ) { index ->
                likeUsers[index]?.let { user ->
                    LikeUserItem(
                        likeUser = user,
                        isMine = user.memberId == currentMemberId,
                        onProfileClick = onProfileClick,
                        onFollowClick = { onFollowClick(user) }
                    )
                }
            }
        }
    }
}


@Composable
private fun LikeUserItem(
    likeUser: LikeUser,
    isMine: Boolean,
    onProfileClick: (String) -> Unit,
    onFollowClick: (LikeUser) -> Unit
) {
    Surface(
        color = White_FFFFFF,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentHeight()
        ) {
            // profile
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${BuildConfig.BASE_URL}/images/${likeUser.profileImg}")
                    .build(),
                contentDescription = "${likeUser.nickname} + profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(shape = CircleShape)
                    .align(Alignment.CenterVertically)
                    .clickableSingle(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onProfileClick(likeUser.memberId) }
                    )
            )

            // nickname
            Text(
                text = likeUser.nickname,
                style = DayoTheme.typography.b6.copy(Dark),
                modifier = Modifier.clickableSingle(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onProfileClick(likeUser.memberId) }
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            if (!isMine) {
                if (!likeUser.follow) {
                    FilledButton(
                        onClick = { onFollowClick(likeUser) },
                        modifier = Modifier.height(36.dp),
                        label = stringResource(id = R.string.follow_yet),
                        icon = { Icon(Icons.Filled.Add, stringResource(id = R.string.follow_yet)) },
                        isTonal = true
                    )
                } else {
                    DayoOutlinedButton(
                        onClick = { onFollowClick(likeUser) },
                        modifier = Modifier.height(36.dp),
                        label = stringResource(id = R.string.follow_already),
                        icon = { Icon(Icons.Filled.Check, stringResource(id = R.string.follow_already)) })
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPostLikeUsersScreen() {
    val likeUsers = flowOf(PagingData.empty<LikeUser>()).collectAsLazyPagingItems()
    PostLikeUsersScreen(
        currentMemberId = "",
        likeCount = 1,
        likeUsers = likeUsers,
        onProfileClick = { },
        onFollowClick = { },
        onBackClick = { }
    )
}