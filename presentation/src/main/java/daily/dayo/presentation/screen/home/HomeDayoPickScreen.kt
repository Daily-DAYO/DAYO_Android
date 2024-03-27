package daily.dayo.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.caption1
import daily.dayo.presentation.theme.caption3
import daily.dayo.presentation.view.EmojiView
import daily.dayo.presentation.view.FilledButton
import daily.dayo.presentation.view.HomePostView
import daily.dayo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeDayoPickScreen(
    selectedCategoryName: String,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val dayoPickPostList = homeViewModel.dayoPickPostList.observeAsState()
    val refreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(refreshing, { homeViewModel.loadDayoPickPosts() })
    var isEmpty by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 18.dp),
                modifier = Modifier.wrapContentHeight()
            ) {
                // description
                item(span = { GridItemSpan(maxLineSpan) }) {
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
                                emoji = "\uD83D\uDCA1",
                                emojiSize = MaterialTheme.typography.bodyMedium.fontSize,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )

                            Text(
                                text = stringResource(id = R.string.home_dayopick_description),
                                style = MaterialTheme.typography.bodyMedium.copy(Color(0xFF73777C)),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        CategoryButton(selectedCategoryName, coroutineScope, bottomSheetState)
                    }
                }

                // dayo pick list
                when (dayoPickPostList.value?.status) {
                    Status.SUCCESS -> {
                        isEmpty = dayoPickPostList.value?.data?.isEmpty() == true
                        dayoPickPostList.value?.data?.mapIndexed { index, post ->
                            item {
                                HomePostView(
                                    post = post,
                                    isDayoPick = index in 0..4,
                                    modifier = Modifier.padding(bottom = 20.dp),
                                    onClickPost = {
                                        // todo move to post
                                    },
                                    onClickLikePost = {
                                        if (!post.heart) {
                                            homeViewModel.requestLikePost(post.postId!!, isDayoPickLike = true)
                                        } else {
                                            homeViewModel.requestUnlikePost(post.postId!!, isDayoPickLike = true)
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

            // empty view
            if (isEmpty) HomeDayoPickEmptyView()
        }

        // refresh indicator
        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun HomeDayoPickEmptyView() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_home_empty), contentDescription = null)
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = stringResource(id = R.string.home_dayopick_empty_title), style = MaterialTheme.typography.b3.copy(Gray3_9FA5AE))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = stringResource(id = R.string.home_dayopick_empty_detail), style = MaterialTheme.typography.caption1.copy(Gray4_C5CAD2))

        Spacer(modifier = Modifier.height(28.dp))
        FilledButton(onClick = { /*TODO*/ }, label = stringResource(id = R.string.home_dayopick_empty_action))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryButton(
    selectedCategory: String,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    Button(
        onClick = { coroutineScope.launch { bottomSheetState.show() } },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 4.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = White_FFFFFF, contentColor = Gray2_767B83),
        modifier = Modifier
            .border(1.dp, Gray6_F0F1F3, shape = RoundedCornerShape(8.dp))
    ) {
        Text(text = selectedCategory, style = MaterialTheme.typography.caption3)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Filled.ArrowDropDown, "category menu")
    }
}

