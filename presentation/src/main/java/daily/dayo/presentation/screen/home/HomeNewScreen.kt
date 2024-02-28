package daily.dayo.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.view.EmojiView
import daily.dayo.presentation.view.HomePostView
import daily.dayo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeNewScreen(
    selectedCategoryName: String,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val newPostList = homeViewModel.newPostList.observeAsState()
    val refreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(refreshing, { homeViewModel.loadNewPosts() })
    Box(Modifier.pullRefresh(pullRefreshState)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 18.dp),
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            // description
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        EmojiView(
                            emoji = "\uD83D\uDC40",
                            emojiSize = MaterialTheme.typography.bodyMedium.fontSize,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Text(
                            text = stringResource(id = R.string.home_new_description),
                            style = MaterialTheme.typography.bodyMedium.copy(Color(0xFF73777C)),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    CategoryButton(selectedCategoryName, coroutineScope, bottomSheetState)
                }
            }

            // new post list
            when (newPostList.value?.status) {
                Status.SUCCESS -> {
                    newPostList.value?.data?.mapIndexed { index, post ->
                        item {
                            HomePostView(
                                post = post,
                                modifier = Modifier.padding(bottom = 20.dp),
                                onClickPost = {
                                    // todo move to post
                                },
                                onClickLikePost = {
                                    if (!post.heart) {
                                        homeViewModel.requestLikePost(post.postId!!, isDayoPickLike = false)
                                    } else {
                                        homeViewModel.requestUnlikePost(post.postId!!, isDayoPickLike = false)
                                    }
                                },
                                onClickNickname = {
                                    // todo move to profile
                                }
                            )
                        }
                    }
                }

                Status.LOADING -> {
                }

                Status.ERROR -> {

                }

                else -> {}
            }
        }

        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}