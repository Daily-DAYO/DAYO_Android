package daily.dayo.presentation.screen.settings

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.size.Size
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.view.DayoOutlinedButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun BlockedUsersScreen(
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    profileSettingViewModel: ProfileSettingViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val blockedUsers by profileSettingViewModel.blockList.collectAsStateWithLifecycle()
    val unblockSuccess by profileViewModel.unblockSuccess.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileSettingViewModel.requestBlockList()
    }

    LaunchedEffect(unblockSuccess) {
        unblockSuccess?.let { state ->
            when (state) {
                Status.SUCCESS -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(context.getString(R.string.other_profile_unblock_success_message))
                    }
                    profileSettingViewModel.requestBlockList()
                }

                Status.ERROR -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(context.getString(R.string.other_profile_unblock_fail_message))
                    }
                }

                Status.LOADING -> {

                }
            }
        }
    }

    Scaffold(
        topBar = { BlockedUsersActionbarLayout(onBackClick = onBackClick) },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .background(DayoTheme.colorScheme.background)
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    if (blockedUsers.status != Status.ERROR) {
                        blockedUsers.data.orEmpty().let { blockedUsers ->
                            if (blockedUsers.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 164.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_blocked_users_empty),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .width(136.dp)
                                                .wrapContentHeight()
                                                .padding(6.5.dp)
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Text(
                                            text = stringResource(R.string.blocked_users_empty_description),
                                            color = Gray3_9FA5AE,
                                            style = DayoTheme.typography.b3,
                                            modifier = Modifier
                                                .wrapContentSize()
                                        )
                                    }
                                }
                            } else {
                                itemsIndexed(
                                    blockedUsers,
                                    key = { _, user -> user.memberId }
                                ) { _, user ->
                                    // Nickname이 null인 경우는 없을 것 같지만, null일 경우 보이지 않도록 처리
                                    user.nickname?.let { nickname ->
                                        BlockedUser(
                                            userId = user.memberId,
                                            imageFileName = user.profileImg,
                                            nickName = nickname,
                                            onUnblockClick = { userId ->
                                                coroutineScope.launch {
                                                    profileViewModel.requestUnblockMember(userId)
                                                }
                                            },
                                            context = context,
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // TODO: Handle error state
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun BlockedUser(
    userId: String = "",
    imageFileName: String = "",
    nickName: String = "",
    onUnblockClick: (String) -> Unit = {},
    context: Context = LocalContext.current,
) {
    Row(
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .fillMaxWidth()
            .height(38.dp)
            .padding(bottom = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundImageView(
            imageUrl = "${BuildConfig.BASE_URL}/images/${imageFileName}",
            context = context,
            modifier = Modifier
                .size(36.dp)
                .aspectRatio(1f),
            imageDescription = "User Profile Image",
            imageSize = Size(36, 36),
            roundSize = 18.dp,
            placeholderResId = R.drawable.ic_profile_default_user_profile,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = nickName,
            style = DayoTheme.typography.b6.copy(color = Dark),
            maxLines = 1,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        DayoOutlinedButton(
            label = stringResource(R.string.blocked_users_unblock),
            onClick = { onUnblockClick(userId) },
            modifier = Modifier.height(36.dp),
        )
    }
}


@Preview
@Composable
fun BlockedUsersActionbarLayout(
    onBackClick: () -> Unit = {},
) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        title = stringResource(R.string.blocked_users_title),
    )
}