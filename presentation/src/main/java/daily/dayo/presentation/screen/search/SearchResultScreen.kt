package daily.dayo.presentation.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import daily.dayo.domain.model.Search
import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.domain.model.SearchUser
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.common.toSp
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.FollowViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SearchResultRoute(
    searchKeyword: String,
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel(),
    followViewModel: FollowViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val searchKeywordResultsTag = searchViewModel.searchTagList.collectAsLazyPagingItems()
    val searchKeywordResultsUser = searchViewModel.searchUserList.collectAsLazyPagingItems()
    val searchKeywordResultsTagTotalCount by searchViewModel.searchTagTotalCount.collectAsStateWithLifecycle(
        0
    )
    val searchKeywordResultsUserTotalCount by searchViewModel.searchUserTotalCount.collectAsStateWithLifecycle(
        0
    )
    val followSuccess by followViewModel.followingFollowSuccess.observeAsState(Event(true))
    val unFollowSuccess by followViewModel.followerUnfollowSuccess.observeAsState(Event(true))
    val currentUserMemberId = accountViewModel.getCurrentUserInfo().memberId

    LaunchedEffect(Unit) {
        with(searchViewModel) {
            searchKeyword(keyword = searchKeyword, SearchHistoryType.TAG)
            searchKeyword(keyword = searchKeyword, SearchHistoryType.USER)
        }
    }

    SearchResultScreen(
        searchKeyword = searchKeyword,
        searchKeywordResultsTag = searchKeywordResultsTag,
        searchKeywordResultsTagTotalCount = searchKeywordResultsTagTotalCount,
        searchKeywordResultsUser = searchKeywordResultsUser,
        searchKeywordResultsUserTotalCount = searchKeywordResultsUserTotalCount,
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSearchClick = { keyword ->
            searchViewModel.searchKeyword(keyword, SearchHistoryType.TAG)
            searchViewModel.searchKeyword(keyword, SearchHistoryType.USER)
        },
        onFollowClick = { memberId, isFollower ->
            followViewModel.requestFollow(
                memberId,
                isFollower
            )
        },
        onUnFollowClick = { memberId, isFollower ->
            followViewModel.requestUnfollow(
                memberId,
                isFollower
            )
        },
        onFollowSuccess = followSuccess.peekContent(),
        onUnFollowSuccess = unFollowSuccess.peekContent(),
        currentUserMemberId = currentUserMemberId ?: ""
    )
}

@Composable
@Preview
internal fun SearchResultRoutePreview() {
    SearchResultScreen(
        searchKeyword = "", onBackClick = { },
        searchKeywordResultsTag = null,
        searchKeywordResultsUser = null,
        searchKeywordResultsTagTotalCount = 0,
        searchKeywordResultsUserTotalCount = 0,
        onSearchClick = { _ -> },
        onPostClick = { },
        onFollowClick = { _, _ -> },
        onUnFollowClick = { _, _ -> },
        currentUserMemberId = ""
    )
}

@Composable
fun SearchResultScreen(
    searchKeyword: String,
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit,
    searchKeywordResultsTag: LazyPagingItems<Search>?,
    searchKeywordResultsTagTotalCount: Int,
    searchKeywordResultsUser: LazyPagingItems<SearchUser>?,
    searchKeywordResultsUserTotalCount: Int,
    onSearchClick: (String) -> Unit,
    onFollowClick: (String, Boolean) -> Unit,
    onUnFollowClick: (String, Boolean) -> Unit,
    onFollowSuccess: Boolean = true,
    onUnFollowSuccess: Boolean = true,
    currentUserMemberId: String
) {
    val pages = listOf("태그", "사용자")
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }

    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchActionbarLayout(
                initialKeyword = searchKeyword,
                onBackClick = onBackClick,
                onSearchClick = { keyword ->
                    onSearchClick(keyword)
                    onSearchClick(keyword)
                }
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
                    .padding(18.dp, 0.dp, 18.dp, 0.dp),
            ) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                style = TextStyle(
                                    fontSize = 14.dp.toSp(),
                                    fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                                    fontWeight = FontWeight(500)
                                )
                            )
                        },
                        selected = pagerState.currentPage == index,
                        selectedContentColor = Primary_23C882,
                        unselectedContentColor = Gray2_767B83,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
            HorizontalPager(
                modifier = Modifier,
                state = pagerState,
                pageSpacing = 0.dp,
                userScrollEnabled = false,
                reverseLayout = false,
                contentPadding = PaddingValues(0.dp),
                beyondViewportPageCount = 0,
                pageSize = PageSize.Fill,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                key = null,
                pageContent = { page ->
                    if (searchKeywordResultsTag?.loadState?.refresh is LoadState.NotLoading ||
                        searchKeywordResultsUser?.loadState?.refresh is LoadState.NotLoading
                    ) {
                        when (page) {
                            0 -> {
                                if (searchKeywordResultsTagTotalCount != 0) {
                                    Column {
                                        SearchResultsCount(resultCount = searchKeywordResultsTagTotalCount)
                                        SearchResultTagView(
                                            searchKeywordResultsTag = searchKeywordResultsTag,
                                            onPostClick = onPostClick
                                        )
                                    }
                                } else {
                                    SearchResultEmpty()
                                }
                            }

                            1 -> {
                                if (searchKeywordResultsUserTotalCount != 0) {
                                    Column {
                                        SearchResultsCount(resultCount = searchKeywordResultsUserTotalCount)
                                        SearchResultsUserView(
                                            searchKeywordResultsUser = searchKeywordResultsUser,
                                            onFollowClick = onFollowClick,
                                            onUnFollowClick = onUnFollowClick,
                                            onFollowSuccess = onFollowSuccess,
                                            onUnFollowSuccess = onUnFollowSuccess,
                                            currentUserMemberId = currentUserMemberId
                                        )
                                    }
                                } else {
                                    SearchResultEmpty()
                                }
                            }

                            else -> {}
                        }
                    }
                }
            )
        }
    }
}

@Composable
@Preview
fun SearchResultEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_search_empty),
            contentDescription = "search tag empty"
        )
        Text(
            text = stringResource(R.string.search_result_empty_title),
            style = DayoTheme.typography.b3
                .copy(
                    color = Gray3_9FA5AE,
                    textAlign = TextAlign.Center
                ),
        )
        Text(
            modifier = Modifier.padding(vertical = 2.dp),
            text = stringResource(id = R.string.search_result_empty_description),
            style = DayoTheme.typography.caption1
                .copy(
                    color = Gray3_9FA5AE,
                    textAlign = TextAlign.Center
                ),
        )
    }
}

@Composable
@Preview
fun SearchResultsCount(resultCount: Int = 0) {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                    fontWeight = FontWeight(500),
                    color = Primary_23C882
                ),
                text = "$resultCount",
                modifier = Modifier.padding(end = 2.dp)
            )
            Text(
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                    fontWeight = FontWeight(500)
                ),
                text = "개의 검색결과"
            )
        }
    }
}

@Composable
fun SearchResultTagView(
    searchKeywordResultsTag: LazyPagingItems<Search>?,
    onPostClick: (String) -> Unit
) {
    val imageInteractionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 18.dp),
            modifier = Modifier
                .wrapContentHeight()
        ) {
            searchKeywordResultsTag?.let { posts ->
                items(
                    count = posts.itemCount,
                    key = posts.itemKey()
                ) { index ->
                    val item = posts[index]
                    item?.let { post ->
                        RoundImageView(
                            context = LocalContext.current,
                            imageUrl = "${BuildConfig.BASE_URL}/images/${post.thumbnailImage}",
                            imageDescription = "searched Image",
                            modifier = Modifier
                                .matchParentSize()
                                .clickableSingle(
                                    interactionSource = imageInteractionSource,
                                    indication = null,
                                    onClick = { onPostClick(post.postId.toString()) }
                                )
                        )
                    }
                }
            }
        }
    }
}

private fun onClickProfile(userId: String) {
    // TODO Navigate to Profile
}

@Composable
fun SearchResultsUserView(
    searchKeywordResultsUser: LazyPagingItems<SearchUser>?,
    onFollowClick: (String, Boolean) -> Unit,
    onUnFollowClick: (String, Boolean) -> Unit,
    onFollowSuccess: Boolean = true,
    onUnFollowSuccess: Boolean = true,
    currentUserMemberId: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DayoTheme.colorScheme.background)
    ) {
        searchKeywordResultsUser?.let { users ->

            items(users.itemCount) { index ->
                val item = users[index]
                SearchResultUserView(
                    user = item!!,
                    onFollowClick,
                    onUnFollowClick,
                    onFollowSuccess,
                    onUnFollowSuccess,
                    currentUserMemberId
                )
            }
        }
    }
}

@Composable
@Preview
fun SearchResultUserViewPreview() {
    SearchResultUserView(
        user = SearchUser(
            memberId = "1",
            profileImg = "profileImg",
            nickname = "nickname",
            isFollow = false
        ),
        onFollowClick = { _, _ -> },
        onUnFollowClick = { _, _ -> },
        onFollowSuccess = true,
        onUnFollowSuccess = true,
        currentUserMemberId = "1"
    )
}

@Composable
fun SearchResultUserView(
    user: SearchUser,
    onFollowClick: (String, Boolean) -> Unit,
    onUnFollowClick: (String, Boolean) -> Unit,
    onFollowSuccess: Boolean, onUnFollowSuccess: Boolean,
    currentUserMemberId: String
) {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickableSingle { onClickProfile(userId = user.memberId) }
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            SearchResultUserImageLayout(user = user)
            SearchResultUserNicknameLayout(userNickname = user.nickname) // TODO NICKNAME을 받아오도록해야 함
            Spacer(modifier = Modifier.weight(1f))
            if (user.memberId != currentUserMemberId) {
                SearchResultUserFollowLayout(
                    user = user,
                    onFollowClick,
                    onUnFollowClick,
                    onFollowSuccess,
                    onUnFollowSuccess
                )
            }
        }
    }
}

@Composable
private fun SearchResultUserImageLayout(user: SearchUser) {
    val imageInteractionSource = remember { MutableInteractionSource() }
    GlideImage(
        imageModel = { "${BuildConfig.BASE_URL}/images/${user.profileImg}" },
        imageOptions = ImageOptions(
            contentDescription = "image description",
            contentScale = ContentScale.Crop,
        ),
        modifier = Modifier
            .padding(1.dp)
            .height(36.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickableSingle(
                interactionSource = imageInteractionSource,
                indication = null,
                onClick = { onClickProfile(user.memberId) }
            )
    )
}

@Composable
private fun SearchResultUserNicknameLayout(userNickname: String) {
    Text(
        text = userNickname,
        style = DayoTheme.typography.b6.copy(color = Gray1_50545B),
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
    )
}

@Composable
@Preview
private fun SearchResultUserFollowLayoutPreviewFalse() {
    SearchResultUserFollowLayout(
        user = SearchUser(
            memberId = "1",
            profileImg = "profileImg",
            nickname = "nickname",
            isFollow = false
        ),
        onFollowClick = { _, _ -> },
        onUnFollowClick = { _, _ -> },
        followSuccess = true,
        unFollowSuccess = true
    )
}

@Composable
@Preview
private fun SearchResultUserFollowLayoutPreviewTrue() {
    SearchResultUserFollowLayout(
        user = SearchUser(
            memberId = "1",
            profileImg = "profileImg",
            nickname = "nickname",
            isFollow = true
        ),
        onFollowClick = { _, _ -> },
        onUnFollowClick = { _, _ -> },
        followSuccess = true,
        unFollowSuccess = true
    )
}

@Composable
private fun SearchResultUserFollowLayout(
    user: SearchUser,
    onFollowClick: (String, Boolean) -> Unit,
    onUnFollowClick: (String, Boolean) -> Unit,
    followSuccess: Boolean,
    unFollowSuccess: Boolean
) {
    val followInteractionSource = remember { MutableInteractionSource() }
    val followIsPressed by followInteractionSource.collectIsPressedAsState()
    var followState by rememberSaveable { mutableStateOf(user.isFollow) }

    var isFollowButton by remember {
        mutableStateOf((!followState and !followIsPressed) or (followState and followIsPressed))
    }
    LaunchedEffect(followIsPressed, followState) {
        isFollowButton = (!followState and !followIsPressed) or (followState and followIsPressed)
    }

    NoRippleIconButton(
        onClick = {
            if (followState) {
                onUnFollowClick(user.memberId, false)
                if (unFollowSuccess) {
                    followState = false
                }
            } else {
                onFollowClick(user.memberId, false)
                if (followSuccess) {
                    followState = true
                }
            }
        },
        iconContentDescription = "follow button",
        iconPainter = if (isFollowButton) painterResource(R.drawable.ic_plus_sign_green)
        else painterResource(R.drawable.ic_check_sign_gray),
        iconButtonModifier = Modifier
            .width(85.dp)
            .height(36.dp)
            .border(
                width = 1.dp,
                color = if (isFollowButton) PrimaryL3_F2FBF7 else Gray5_E8EAEE,
                shape = RoundedCornerShape(32.dp)
            )
            .background(
                color = if (isFollowButton) PrimaryL3_F2FBF7 else White_FFFFFF,
                shape = RoundedCornerShape(32.dp)
            ),
        iconModifier = Modifier
            .padding(1.dp)
            .width(20.dp)
            .height(20.dp),
        iconTintColor = if (isFollowButton) Primary_23C882 else Gray2_767B83,
        interactionSource = followInteractionSource
    ) {
        Text(
            text = if (isFollowButton) stringResource(id = R.string.follow_yet)
            else stringResource(id = R.string.follow_already),
            style = DayoTheme.typography.b6.copy(
                color = if (isFollowButton) Primary_23C882 else Gray2_767B83,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .height(20.dp),
        )
    }
}
