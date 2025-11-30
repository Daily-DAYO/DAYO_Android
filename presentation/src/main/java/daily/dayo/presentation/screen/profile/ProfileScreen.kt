package daily.dayo.presentation.screen.profile

import android.content.Context
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
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
import daily.dayo.presentation.view.DayoOutlinedButton
import daily.dayo.presentation.view.FilledButton
import daily.dayo.presentation.view.FolderView
import daily.dayo.presentation.view.ProfileDropdownMenu
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.ConfirmDialog
import daily.dayo.presentation.view.dialog.UserReportDialog
import daily.dayo.presentation.viewmodel.FolderViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel
import daily.dayo.presentation.viewmodel.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    externalSnackBarHostState: SnackbarHostState,
    memberId: String,
    onFollowMenuClick: (String, Int) -> Unit,
    onFolderClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    folderViewModel: FolderViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val profileInfo = profileViewModel.profileInfo.observeAsState()
    val folderList = folderViewModel.folderList.observeAsState()
    val onFollowClick: () -> Unit = {
        profileViewModel.toggleFollow(memberId, profileInfo.value?.data?.follow ?: false)
    }
    val onClickUserReport: (String) -> Unit = { reason ->
        profileInfo.value?.data?.memberId?.let { reportViewModel.requestSaveMemberReport(reason, it) }
    }
    val onClickUserBlockSuccess by profileViewModel.blockSuccess.collectAsStateWithLifecycle()
    val onClickUserBlock: (String) -> Unit = { memberId ->
        profileInfo.value?.data?.memberId?.let { profileViewModel.requestBlockMember(it) }
    }

    LaunchedEffect(onClickUserBlockSuccess) {
        when (onClickUserBlockSuccess) {
            Status.SUCCESS -> {
                coroutineScope.launch {
                    externalSnackBarHostState.showSnackbar(context.getString(R.string.other_profile_block_success_message))
                }
                onBackClick()
            }

            Status.ERROR -> {
            }

            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.requestOtherProfile(memberId)
        folderViewModel.requestUserFolderList(memberId)

        launch {
            profileViewModel.followSuccess
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { followSuccess ->
                    if (followSuccess) {
                        profileViewModel.requestOtherProfile(memberId)
                    }
                }
        }

        launch {
            profileViewModel.unfollowSuccess
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { unfollowSuccess ->
                    if (unfollowSuccess) {
                        profileViewModel.requestOtherProfile(memberId)
                    }
                }
        }
    }

    ProfileScreen(
        context = context,
        coroutineScope = coroutineScope,
        snackBarHostState = snackBarHostState,
        profile = profileInfo.value?.data ?: DEFAULT_PROFILE,
        folderList = folderList.value?.data ?: emptyList(),
        onFollowClick = onFollowClick,
        onFollowMenuClick = onFollowMenuClick,
        onFolderClick = onFolderClick,
        onClickUserReport = onClickUserReport,
        onClickUserBlock = onClickUserBlock,
        onBackClick = onBackClick
    )
}

@Composable
private fun ProfileScreen(
    context: Context,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    profile: Profile,
    folderList: List<Folder>,
    onFollowClick: () -> Unit,
    onFollowMenuClick: (String, Int) -> Unit,
    onFolderClick: (Long) -> Unit,
    onClickUserReport: (String) -> Unit,
    onClickUserBlock: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ProfileTopNavigation(
                onUserReportClick = { showReportDialog = true },
                onUserBlockClick = { showBlockDialog = true },
                onBackClick = onBackClick,
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
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

                item(span = { GridItemSpan(2) }) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        UserFollowButton(profile.follow ?: false, onFollowClick)
                        Spacer(Modifier.height(20.dp))
                    }
                }

                items(folderList) { folder ->
                    UserDiary(folder, onFolderClick)
                }
            }
        }
    )

    if (showReportDialog) {
        UserReportDialog(
            onClickCancel = { showReportDialog = false },
            onClickConfirm = { reason ->
                onClickUserReport(reason)
                showReportDialog = false
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.report_user_alert_message))
                }
            }
        )
    }

    if (showBlockDialog) {
        ConfirmDialog(
            title = stringResource(id = R.string.other_profile_block_message),
            description = stringResource(id = R.string.other_profile_block_explanation_message),
            onClickCancel = { showBlockDialog = false },
            onClickConfirm = {
                onClickUserBlock(profile.memberId ?: "")
                showBlockDialog = false
            },
        )
    }
}

@Composable
private fun UserFollowButton(
    isFollow: Boolean,
    onFollowClick: () -> Unit
) {
    if (isFollow) {
        DayoOutlinedButton(
            onClick = onFollowClick,
            label = stringResource(id = R.string.follow_already),
            modifier = Modifier.fillMaxWidth(),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_2_gray),
                    contentDescription = stringResource(R.string.follow_already_icon_description),
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    } else {
        FilledButton(
            onClick = onFollowClick,
            label = stringResource(id = R.string.follow_yet),
            modifier = Modifier.fillMaxWidth(),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus_green),
                    contentDescription = stringResource(R.string.follow_yet_icon_description),
                    modifier = Modifier.size(20.dp)
                )
            },
            isTonal = true
        )
    }
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

        // nickname
        Text(
            text = profile.nickname,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = DayoTheme.typography.h1.copy(
                color = Dark,
                fontWeight = FontWeight.SemiBold
            )
        )

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
private fun UserDiary(folder: Folder, onFolderClick: (Long) -> Unit) {
    FolderView(
        folder = folder,
        onClickFolder = onFolderClick,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun ProfileTopNavigation(
    onUserReportClick: () -> Unit,
    onUserBlockClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    var showProfileOption by remember { mutableStateOf(false) }

    TopNavigation(
        leftIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = stringResource(id = R.string.back_sign),
                    tint = Dark
                )
            }
        },
        rightIcon = {
            Box {
                IconButton(
                    onClick = { showProfileOption = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_option_horizontal),
                        contentDescription = "profile option",
                        modifier = Modifier.padding(8.dp),
                        tint = Gray2_767B83,
                    )
                }
            }

            ProfileDropdownMenu(
                expanded = showProfileOption,
                onDismissRequest = { showProfileOption = !showProfileOption },
                onUserReportClick = {
                    onUserReportClick()
                    showProfileOption = !showProfileOption
                },
                onUserBlockClick = {
                    onUserBlockClick()
                    showProfileOption = !showProfileOption
                }
            )
        }
    )
}

@Preview
@Composable
private fun PreviewProfileScreen() {
    ProfileScreen(
        context = LocalContext.current,
        coroutineScope = rememberCoroutineScope(),
        snackBarHostState = remember { SnackbarHostState() },
        profile = DEFAULT_PROFILE,
        folderList = emptyList(),
        onFollowClick = { },
        onFollowMenuClick = { _, _ -> },
        onFolderClick = { },
        onClickUserReport = { },
        onClickUserBlock = { },
        onBackClick = { },
    )
}

@Preview
@Composable
private fun PreviewUserFollowButton() {
    Column {
        UserFollowButton(
            isFollow = true,
            onFollowClick = {}
        )

        UserFollowButton(
            isFollow = false,
            onFollowClick = {}
        )
    }
}

private const val FOLLOWER = 0
private const val FOLLOWING = 1
private val DEFAULT_PROFILE = Profile(
    memberId = null,
    email = "",
    nickname = "",
    profileImg = "",
    postCount = 10,
    followerCount = 10,
    followingCount = 10,
    follow = null,
)

