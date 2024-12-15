package daily.dayo.presentation.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Follow
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoOutlinedButton
import daily.dayo.presentation.view.FilledButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.FollowUiState
import daily.dayo.presentation.viewmodel.FollowViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
internal fun FollowScreen(
    memberId: String,
    tabNum: Int,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    followViewModel: FollowViewModel = hiltViewModel()
) {
    val profileInfo by profileViewModel.profileInfo.observeAsState()
    val followerUiState by followViewModel.followerUiState.observeAsState(FollowUiState.Loading)
    val followingUiState by followViewModel.followingUiState.observeAsState(FollowUiState.Loading)
    val pagerState = rememberPagerState(initialPage = tabNum, pageCount = { 2 })
    val onFollowClick: (Follow) -> Unit = { follow ->
        followViewModel.toggleFollow(follow, pagerState.currentPage == FOLLOWER_TAB_ID)
    }

    LaunchedEffect(memberId) {
        profileViewModel.requestOtherProfile(memberId)
        // 팔로워, 팔로잉 수를 알기 위함, pagerState에 따른 중복 호출 방지를 위해 초기 탭이 아닌 정보만 호출
        when (tabNum) {
            FOLLOWER_TAB_ID -> followViewModel.requestFollowingList(memberId)
            FOLLOWING_TAB_ID -> followViewModel.requestFollowerList(memberId)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                when (page) {
                    FOLLOWER_TAB_ID -> followViewModel.requestFollowerList(memberId)
                    FOLLOWING_TAB_ID -> followViewModel.requestFollowingList(memberId)
                }
            }
    }

    FollowScreen(
        profileInfo?.data,
        followerUiState,
        followingUiState,
        pagerState,
        onFollowClick,
        onBackClick
    )
}

@Composable
private fun FollowScreen(
    profileInfo: Profile?,
    followerUiState: FollowUiState,
    followingUiState: FollowUiState,
    pagerState: PagerState,
    onFollowClick: (Follow) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.indication(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_sign),
                            contentDescription = stringResource(id = R.string.back_sign),
                            tint = Gray1_50545B
                        )
                    }
                },
                title = profileInfo?.nickname ?: "",
                titleAlignment = TopNavigationAlign.CENTER
            )
        },
        containerColor = White
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            val followerCount = when (followerUiState) {
                is FollowUiState.Success -> followerUiState.count
                is FollowUiState.Loading,
                is FollowUiState.Error -> 0
            }

            val followingCount = when (followingUiState) {
                is FollowUiState.Success -> followingUiState.count
                is FollowUiState.Loading,
                is FollowUiState.Error -> 0
            }

            FollowTab(pagerState, followerCount, followingCount)

            HorizontalPager(
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                pageContent = { page ->
                    when (page) {
                        FOLLOWER_TAB_ID -> FollowContent(page, followerUiState, onFollowClick)
                        FOLLOWING_TAB_ID -> FollowContent(page, followingUiState, onFollowClick)
                    }
                }
            )
        }
    }
}

@Composable
private fun FollowContent(
    tabNum: Int,
    followUiState: FollowUiState,
    onFollowClick: (Follow) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DayoTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 20.dp, horizontal = 18.dp)
    ) {
        when (followUiState) {
            is FollowUiState.Loading -> {

            }

            is FollowUiState.Success -> {
                if (followUiState.data.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            FollowerEmpty(tabNum)
                        }
                    }
                } else {
                    items(items = followUiState.data, key = { it.memberId }) { follow ->
                        FollowUserInfo(follow, onFollowClick)
                    }
                }
            }

            is FollowUiState.Error -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FollowerEmpty(tabNum)
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowerEmpty(tabNum: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_follow_empty),
                contentDescription = stringResource(id = R.string.follower_empty_description)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(
                    if (tabNum == FOLLOWER_TAB_ID) R.string.follower_empty_description
                    else R.string.following_empty_description
                ),
                modifier = Modifier.padding(bottom = 2.dp),
                color = Gray3_9FA5AE,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Visible,
                style = DayoTheme.typography.b3,
            )

            Text(
                text = stringResource(
                    if (tabNum == FOLLOWER_TAB_ID) R.string.follower_empty_sub_description
                    else R.string.following_empty_sub_description
                ),
                color = Gray4_C5CAD2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Visible,
                style = DayoTheme.typography.caption2,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FollowUserInfo(
    follow: Follow,
    onFollowClick: (Follow) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val context = LocalContext.current
        RoundImageView(
            context = context,
            imageUrl = "${BuildConfig.BASE_URL}/images/${follow.profileImg}",
            roundSize = 18.dp,
            customModifier = Modifier.size(36.dp)
        )

        Text(
            text = follow.nickname,
            modifier = Modifier.weight(1f),
            color = Dark,
            style = DayoTheme.typography.b6
        )

        if (follow.isFollow) {
            DayoOutlinedButton(
                onClick = { onFollowClick(follow) },
                label = stringResource(id = R.string.follow_already),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_sign_gray),
                        contentDescription = stringResource(R.string.follow_already_icon_description),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        } else {
            FilledButton(
                onClick = { onFollowClick(follow) },
                label = stringResource(id = R.string.follow_yet),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus_sign_green),
                        contentDescription = stringResource(R.string.follow_yet_icon_description),
                        modifier = Modifier.size(20.dp)
                    )
                },
                isTonal = true
            )
        }
    }
}

@Composable
private fun FollowTab(pagerState: PagerState, followerCount: Int, followingCount: Int) {
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        stringResource(id = R.string.follower),
        stringResource(id = R.string.following)
    )

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                color = Primary_23C882,
            )
        },
        divider = { Divider(color = Color.Transparent, thickness = 0.dp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 4.dp)
    ) {
        pages.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        text = "$title " + if (index == FOLLOWER_TAB_ID) "$followerCount" else "$followingCount",
                        style = DayoTheme.typography.b5
                    )
                },
                selected = pagerState.currentPage == index,
                selectedContentColor = Primary_23C882,
                unselectedContentColor = Gray2_767B83,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewEmptyFollowScreen() {
    FollowScreen(
        profileInfo = DEFAULT_PROFILE,
        followerUiState = FollowUiState.Success(),
        followingUiState = FollowUiState.Success(),
        pagerState = rememberPagerState(initialPage = FOLLOWER_TAB_ID, pageCount = { 2 }),
        onFollowClick = {},
        onBackClick = {}
    )
}

@Preview
@Composable
private fun PreviewFollowScreen() {
    FollowScreen(
        profileInfo = DEFAULT_PROFILE,
        followerUiState = FollowUiState.Success(
            2, listOf(
                Follow(
                    isFollow = true,
                    memberId = "",
                    nickname = "다요",
                    profileImg = ""
                ),
                Follow(
                    isFollow = false,
                    memberId = "",
                    nickname = "다요다요",
                    profileImg = ""
                )
            )
        ),
        followingUiState = FollowUiState.Success(),
        pagerState = rememberPagerState(initialPage = FOLLOWER_TAB_ID, pageCount = { 2 }),
        onFollowClick = {},
        onBackClick = {}
    )
}

private const val FOLLOWER_TAB_ID = 0
private const val FOLLOWING_TAB_ID = 1
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
