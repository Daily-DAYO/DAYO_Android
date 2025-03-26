package daily.dayo.presentation.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Folder
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.view.FolderView
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.FolderViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    memberId: String,
    onFollowMenuClick: (String, Int) -> Unit,
    onFolderClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    folderViewModel: FolderViewModel = hiltViewModel()
) {
    val profileInfo = profileViewModel.profileInfo.observeAsState()
    val folderList = folderViewModel.folderList.observeAsState()

    LaunchedEffect(Unit) {
        profileViewModel.requestOtherProfile(memberId)
        folderViewModel.requestUserFolderList(memberId)
    }

    ProfileScreen(
        profile = profileInfo.value?.data ?: DEFAULT_PROFILE,
        folderList = folderList.value?.data ?: emptyList(),
        onFollowMenuClick = onFollowMenuClick,
        onFolderClick = onFolderClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun ProfileScreen(
    profile: Profile,
    folderList: List<Folder>,
    onFollowMenuClick: (String, Int) -> Unit,
    onFolderClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            ProfileTopNavigation(onBackClick)
        },
        content = { contentPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(DayoTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 20.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    UserProfile(profile, onFollowMenuClick)
                }

                items(folderList) { folder ->
                    UserDiary(folder, onFolderClick)
                }
            }
        }
    )
}

@Composable
private fun UserProfile(
    profile: Profile,
    onFollowMenuClick: (String, Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DayoTheme.colorScheme.background)
            .padding(top = 8.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // profile image
        RoundImageView(
            context = LocalContext.current,
            imageUrl = "${BuildConfig.BASE_URL}/images/${profile.profileImg}",
            imageDescription = "profile image",
            roundSize = 24.dp,
            modifier = Modifier
                .size(48.dp)
                .clickableSingle(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { }
                )
        )

        // nickname, email
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = profile.nickname,
                style = DayoTheme.typography.h1.copy(
                    color = Dark,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = profile.email,
                style = DayoTheme.typography.caption5.copy(
                    color = Gray4_C5CAD2
                )
            )
        }

        // follower
        Column(
            modifier = Modifier.clickableSingle(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { profile.memberId?.let { onFollowMenuClick(it, FOLLOWER) } }
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_follower),
                    contentDescription = stringResource(id = R.string.follower),
                    modifier = Modifier.size(12.dp)
                )

                Text(
                    text = stringResource(id = R.string.follower),
                    style = DayoTheme.typography.caption4.copy(color = Gray1_50545B)
                )
            }
            Text(
                text = "${profile.followerCount}",
                style = DayoTheme.typography.b6.copy(color = Gray1_50545B)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // following
        Column(
            modifier = Modifier.clickableSingle(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { profile.memberId?.let { onFollowMenuClick(it, FOLLOWING) } }
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_following),
                    contentDescription = stringResource(id = R.string.following),
                    modifier = Modifier.size(12.dp)
                )

                Text(
                    text = stringResource(id = R.string.following),
                    style = DayoTheme.typography.caption4.copy(color = Gray1_50545B)
                )
            }

            Text(
                text = "${profile.followingCount}",
                style = DayoTheme.typography.b6.copy(color = Gray1_50545B)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
private fun UserDiary(folder: Folder, onFolderClick: (String) -> Unit) {
    FolderView(
        folder = folder,
        onClickFolder = onFolderClick,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun ProfileTopNavigation(
    onBackClick: () -> Unit
) {
    var showPostOption by remember { mutableStateOf(false) }
    TopNavigation(
        leftIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_x_sign),
                    contentDescription = stringResource(id = R.string.back_sign),
                    tint = Dark
                )
            }
        },
        rightIcon = {
            Box {
                IconButton(
                    onClick = { showPostOption = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_option_horizontal),
                        contentDescription = "profile option",
                        modifier = Modifier.padding(8.dp),
                        tint = Gray2_767B83,
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewProfileScreen() {
    ProfileScreen(
        profile = DEFAULT_PROFILE,
        folderList = emptyList(),
        onFollowMenuClick = { _, _ -> },
        onFolderClick = { },
        onBackClick = { }
    )
}

private const val FOLLOWER = 0
private const val FOLLOWING = 1
private val DEFAULT_PROFILE = Profile(
    memberId = null,
    email = "",
    nickname = "nickname",
    profileImg = "",
    postCount = 10,
    followerCount = 10,
    followingCount = 10,
    follow = null,
)

