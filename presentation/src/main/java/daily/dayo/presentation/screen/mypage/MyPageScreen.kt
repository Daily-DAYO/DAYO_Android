package daily.dayo.presentation.screen.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.FolderView
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.FolderViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel

@Composable
fun MyPageScreen(
    onBackClick: () -> Unit,
    onFollowButtonClick: (String, Int) -> Unit,
    onProfileEditClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onFolderClick: (String) -> Unit,
    onFolderCreateClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    folderViewModel: FolderViewModel = hiltViewModel()
) {
    val profileInfo = profileViewModel.profileInfo.observeAsState()
    val folderList = folderViewModel.folderList.observeAsState()

    LaunchedEffect(Unit) {
        profileViewModel.requestMyProfile()
        folderViewModel.requestAllMyFolderList()
    }

    Scaffold(
        topBar = { MyPageTopNavigation() },
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
                    MyPageProfile(profileInfo.value?.data, onFollowButtonClick)
                }

                item(span = { GridItemSpan(2) }) {
                    MyPageMenu(onProfileEditClick, onBookmarkClick)
                }

                item(span = { GridItemSpan(2) }) {
                    MyPageDiaryHeader(onFolderCreateClick)
                }

                when (folderList.value?.status) {
                    Status.SUCCESS -> {
                        folderList.value?.data?.let { folders ->
                            items(folders) { folder ->
                                MyPageDiary(folder, onFolderClick)
                            }
                        }
                    }

                    Status.LOADING -> Unit
                    Status.ERROR -> Unit
                    else -> Unit
                }
            }
        }
    )
}

@Composable
private fun MyPageProfile(
    profile: Profile?,
    onFollowButtonClick: (String, Int) -> Unit
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
            imageUrl = "${BuildConfig.BASE_URL}/images/${profile?.profileImg}",
            imageDescription = "my page profile image",
            roundSize = 24.dp,
            customModifier = Modifier
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
                text = profile?.nickname ?: "",
                style = DayoTheme.typography.h1.copy(
                    color = Dark,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = profile?.email ?: "",
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
                onClick = { profile?.memberId?.let { onFollowButtonClick(it, FOLLOWER) } }
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
                text = "${profile?.followerCount ?: "0"}",
                style = DayoTheme.typography.b6.copy(color = Gray1_50545B)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // following
        Column(
            modifier = Modifier.clickableSingle(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { profile?.memberId?.let { onFollowButtonClick(it, FOLLOWING) } }
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
                text = "${profile?.followingCount ?: "0"}",
                style = DayoTheme.typography.b6.copy(color = Gray1_50545B)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
private fun MyPageMenu(
    onProfileEditClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // edit
        androidx.compose.material3.TextButton(
            onClick = onProfileEditClick,
            shape = RoundedCornerShape(size = 12.dp),
            border = BorderStroke(width = 1.dp, color = Gray6_F0F1F3),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = White_FFFFFF,
                contentColor = Gray2_767B83
            ),
            modifier = Modifier
                .height(36.dp)
                .weight(1f),
            content = {
                Text(
                    text = stringResource(id = R.string.my_profile_edit_title),
                    style = DayoTheme.typography.b6.copy(Gray1_50545B),
                )
            }
        )

        // bookmark
        IconButton(
            onClick = { onBookmarkClick() },
            modifier = Modifier
                .background(color = DayoTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                .border(
                    border = BorderStroke(width = 1.dp, color = Gray6_F0F1F3),
                    shape = RoundedCornerShape(size = 12.dp)
                )
                .size(36.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bookmark_default),
                contentDescription = stringResource(id = R.string.bookmark),
                tint = Gray1_50545B
            )
        }
    }
}

@Composable
private fun MyPageDiaryHeader(
    onFolderCreateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.my_profile_my_diary),
            style = DayoTheme.typography.b3.copy(Dark)
        )

        Button(
            onClick = onFolderCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryL3_F2FBF7,
                contentColor = Primary_23C882
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            Icon(
                Icons.Filled.Add,
                stringResource(id = R.string.my_profile_new_folder)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.my_profile_new_folder),
                style = DayoTheme.typography.b6.copy(Primary_23C882)
            )
        }
    }
}

@Composable
private fun MyPageDiary(folder: Folder, onFolderClick: (String) -> Unit) {
    FolderView(
        folder = folder,
        onClickFolder = onFolderClick,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun MyPageTopNavigation() {
    TopNavigation(
        leftIcon = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(start = 18.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.my_page),
                    style = DayoTheme.typography.h1.copy(
                        color = Gray1_50545B,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        rightIcon = {
            NoRippleIconButton(
                onClick = { },
                iconContentDescription = "setting button",
                iconPainter = painterResource(id = R.drawable.ic_setting),
                iconModifier = Modifier
                    .padding(end = 18.dp)
                    .size(24.dp),
                iconTintColor = Dark
            )
        }
    )
}

@Preview
@Composable
private fun PreviewMyPageTopNavigation() {
    MyPageTopNavigation()
}

@Preview
@Composable
private fun PreviewMyPageProfile() {
    MyPageProfile(profile = null, onFollowButtonClick = { memberId, tabNum -> })
}

@Preview
@Composable
private fun PreviewMyPageMenu() {
    MyPageMenu({}, {})
}

@Preview
@Composable
private fun PreviewMyPageDiaryHeader() {
    MyPageDiaryHeader({})
}

private const val FOLLOWER = 0
private const val FOLLOWING = 1
