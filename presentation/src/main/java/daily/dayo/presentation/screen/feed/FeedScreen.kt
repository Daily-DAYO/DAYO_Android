package daily.dayo.presentation.screen.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.home.CategoryMenu
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.caption1
import daily.dayo.presentation.theme.h1
import daily.dayo.presentation.view.CategoryHorizontalGroup
import daily.dayo.presentation.view.FeedPostView
import daily.dayo.presentation.view.FilledButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedScreen(
    snackBarHostState: SnackbarHostState,
    onEmptyViewClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val feedPostList = feedViewModel.feedState.collectAsLazyPagingItems()
    val refreshing by feedViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(refreshing, { feedViewModel.loadFeedPosts() })
    val listState = rememberSaveable(saver = LazyListState.Saver) { feedViewModel.listState }

    LaunchedEffect(true) {
        with(feedViewModel) {
            loadFeedPosts()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Scaffold(
            topBar = {
                TopNavigation(
                    leftIcon = {
                        Text(
                            text = stringResource(id = R.string.feed),
                            modifier = Modifier.padding(start = 18.dp),
                            style = MaterialTheme.typography.h1.copy(
                                color = Dark
                            )
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                // category
                val categoryMenus = listOf(
                    CategoryMenu.All,
                    CategoryMenu.Scheduler,
                    CategoryMenu.StudyPlanner,
                    CategoryMenu.PocketBook,
                    CategoryMenu.SixHoleDiary,
                    CategoryMenu.Digital,
                    CategoryMenu.ETC
                )
                val selectedCategory = remember { mutableStateOf(categoryMenus[0]) }
                CategoryHorizontalGroup(categoryMenus, selectedCategory, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))

                // feed post list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    state = listState
                ) {
                    items(
                        count = feedPostList.itemCount,
                        key = feedPostList.itemKey()
                    ) { index ->
                        val feedPost = feedPostList[index]
                        feedPost?.let { post ->
                            FeedPostView(
                                post = post,
                                snackBarHostState = snackBarHostState,
                                onClickProfile = {
                                    // todo move to profile
                                },
                                onClickPost = {
                                    post.postId?.let { onPostClick(it.toString()) }
                                },
                                onClickLikePost = {
                                    if (!post.heart) {
                                        feedViewModel.requestLikePost(postId = post.postId!!)
                                    } else {
                                        feedViewModel.requestUnlikePost(postId = post.postId!!)
                                    }
                                },
                                onClickBookmark = {
                                    if (!post.bookmark!!) {
                                        feedViewModel.requestBookmarkPost(postId = post.postId!!)
                                    } else {
                                        feedViewModel.requestDeleteBookmarkPost(postId = post.postId!!)
                                    }
                                },
                                onPostLikeUsersClick = onPostLikeUsersClick,
                                onPostHashtagClick = onPostHashtagClick,
                                bottomSheetState = bottomSheetState,
                                bottomSheetContent = bottomSheetContent
                            )
                        }
                    }
                }

                // empty view
                if (feedPostList.loadState.refresh is LoadState.NotLoading && feedPostList.itemCount == 0) {
                    FeedEmptyView(onEmptyViewClick)
                }
            }
        }

        // refresh indicator
        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun FeedEmptyView(onEmptyViewClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_feed_empty), contentDescription = null)
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = stringResource(id = R.string.feed_empty_title), style = MaterialTheme.typography.b3.copy(Gray3_9FA5AE))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = stringResource(id = R.string.feed_empty_description), style = MaterialTheme.typography.caption1.copy(Gray4_C5CAD2))

        Spacer(modifier = Modifier.height(36.dp))
        FilledButton(onClick = onEmptyViewClick, label = stringResource(id = R.string.feed_empty_button))
    }
}
