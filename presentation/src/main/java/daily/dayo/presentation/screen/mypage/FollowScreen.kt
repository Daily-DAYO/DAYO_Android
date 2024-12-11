package daily.dayo.presentation.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.theme.caption2
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

    LaunchedEffect(memberId) {
        profileViewModel.requestOtherProfile(memberId)
        followViewModel.requestFollowerList(memberId)
        followViewModel.requestFollowingList(memberId)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
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
        onBackClick
    )
}

@Composable
private fun FollowScreen(
    profileInfo: Profile?,
    followerUiState: FollowUiState,
    followingUiState: FollowUiState,
    pagerState: PagerState,
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
                        FOLLOWER_TAB_ID -> FollowContent(page, followerUiState)
                        FOLLOWING_TAB_ID -> FollowContent(page, followingUiState)
                    }
                }
            )
        }
    }
}

@Composable
private fun FollowContent(tabNum: Int, followUiState: FollowUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(White_FFFFFF)
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
                    items(followUiState.data.size) { index ->
                        val item = followUiState.data[index]
                        Text(item.nickname)
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
                style = MaterialTheme.typography.b3,
            )

            Text(
                text = stringResource(
                    if (tabNum == FOLLOWER_TAB_ID) R.string.follower_empty_sub_description
                    else R.string.following_empty_sub_description
                ),
                color = Gray4_C5CAD2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.caption2,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
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
                        style = MaterialTheme.typography.b5
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
private fun PreviewFollowScreen() {
    FollowScreen(
        profileInfo = DEFAULT_PROFILE,
        followerUiState = FollowUiState.Success(),
        followingUiState = FollowUiState.Success(),
        pagerState = rememberPagerState(initialPage = FOLLOWER_TAB_ID, pageCount = { 2 }),
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
