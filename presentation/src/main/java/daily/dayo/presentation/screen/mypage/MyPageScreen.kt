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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption5
import daily.dayo.presentation.theme.h1
import daily.dayo.presentation.view.FolderView
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.FolderViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel

@Composable
fun MyPageScreen(
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
                    .background(color = White_FFFFFF)
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 20.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    MyPageProfile(profileInfo.value?.data)
                }

                item(span = { GridItemSpan(2) }) {
                    MyPageMenu()
                }

                item(span = { GridItemSpan(2) }) {
                    MyPageDiaryHeader()
                }

                when (folderList.value?.status) {
                    Status.SUCCESS -> {
                        folderList.value?.data?.let { folders ->
                            items(folders) { folder ->
                                MyPageDiary(folder)
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
private fun MyPageProfile(profile: Profile?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White_FFFFFF)
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
                style = MaterialTheme.typography.h1.copy(
                    color = Dark,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = profile?.email ?: "",
                style = MaterialTheme.typography.caption5.copy(
                    color = Gray4_C5CAD2
                )
            )
        }

        // follower
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_follower),
                contentDescription = "follower",
                modifier = Modifier.clickableSingle { /*TODO*/ }
            )

            Text(
                text = "${profile?.followerCount ?: ""}".padStart(3, '0'),
                style = MaterialTheme.typography.b6.copy(color = Color(0xFF50545B))
            )
        }

        // following
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_following),
                contentDescription = "following",
                modifier = Modifier.clickableSingle { /*TODO*/ }
            )

            Text(
                text = "${profile?.followingCount ?: ""}".padStart(3, '0'),
                style = MaterialTheme.typography.b6.copy(color = Color(0xFF50545B))
            )
        }
    }
}

@Composable
private fun MyPageMenu() {
    Row(
        modifier = Modifier.padding(bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // edit
        androidx.compose.material3.TextButton(
            onClick = { },
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
                    style = MaterialTheme.typography.b6.copy(Gray1_50545B),
                )
            }
        )

        // bookmark
        IconButton(
            onClick = { },
            modifier = Modifier
                .background(color = White_FFFFFF, shape = RoundedCornerShape(12.dp))
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
private fun MyPageDiaryHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.my_profile_my_diary),
            style = MaterialTheme.typography.b3.copy(Dark)
        )

        androidx.compose.material3.Button(
            onClick = {},
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
                style = MaterialTheme.typography.b6.copy(Primary_23C882)
            )
        }
    }
}

@Composable
private fun MyPageDiary(folder: Folder) {
    FolderView(folder = folder, onClickFolder = {}, modifier = Modifier.padding(bottom = 12.dp))
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
                    style = MaterialTheme.typography.h1.copy(
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
    MyPageProfile(profile = null)
}

@Preview
@Composable
private fun PreviewMyPageMenu() {
    MyPageMenu()
}

@Preview
@Composable
private fun PreviewMyPageDiaryHeader() {
    MyPageDiaryHeader()
}