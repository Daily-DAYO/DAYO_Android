package daily.dayo.presentation.screen.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.home.CategoryMenu
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.h1
import daily.dayo.presentation.view.CategoryHorizontalGroup
import daily.dayo.presentation.view.FeedPostView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedScreen(
    navController: NavController,
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val feedPostList = feedViewModel.feedList.asFlow().collectAsLazyPagingItems()
    val refreshing by feedViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(refreshing, { feedViewModel.loadFeedPosts() })

    LaunchedEffect(true) {
        feedViewModel.loadFeedPosts()
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
                                color = Gray1_313131
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
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    items(
                        count = feedPostList.itemCount,
                        key = feedPostList.itemKey()
                    ) { index ->
                        val feedPost = feedPostList[index]
                        feedPost?.let { post ->
                            FeedPostView(post = post, onClickPost = { }, onClickLikePost = { }, onClickNickname = {})
                        }
                    }
                }
            }
        }

        // refresh indicator
        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}