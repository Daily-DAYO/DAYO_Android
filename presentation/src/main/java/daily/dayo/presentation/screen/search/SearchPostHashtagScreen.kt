package daily.dayo.presentation.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.SearchViewModel

@Composable
fun SearchPostHashtagScreen(
    hashtag: String,
    onBackClick: () -> Unit,
    onPostClick: (Long) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val isLatest by rememberSaveable { mutableStateOf(true) } // TODO api 수정 후 구현
    val hashtagPosts = searchViewModel.searchTagList.collectAsLazyPagingItems()
    val hashtagPostsCount by searchViewModel.searchTagTotalCount.collectAsStateWithLifecycle(0)

    LaunchedEffect(Unit) {
        with(searchViewModel) {
            searchHashtag(hashtag = hashtag)
        }
    }

    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .indication(interactionSource = remember { MutableInteractionSource() }, indication = null)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_sign),
                            contentDescription = "back sign",
                            tint = Gray1_50545B
                        )
                    }
                },
                title = "#$hashtag",
                titleAlignment = TopNavigationAlign.CENTER
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 18.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(DayoTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // description
            item(span = { GridItemSpan(2) }) {
                SearchResultDescription(hashtagPostsCount, isLatest)
            }

            // posts
            items(
                count = hashtagPosts.itemCount,
                key = hashtagPosts.itemKey()
            ) { index ->
                val item = hashtagPosts[index]
                item.let { post ->
                    RoundImageView(
                        context = LocalContext.current,
                        imageUrl = "${BuildConfig.BASE_URL}/images/${post?.thumbnailImage}",
                        imageDescription = "searched Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickableSingle(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { post?.postId?.let { onPostClick(it) } }
                            )
                            .padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultDescription(resultCount: Int, isLatest: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .weight(1f)
        ) {
            Text(
                style = DayoTheme.typography.caption1.copy(color = Primary_23C882),
                text = "$resultCount",
                modifier = Modifier.padding(end = 2.dp)
            )
            Text(
                style = DayoTheme.typography.caption1.copy(color = Gray2_767B83),
                text = "개의 태그"
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_swap_vertical),
                contentDescription = if (isLatest) "최신순" else "오래된순",
                tint = Gray1_50545B
            )
            Text(
                style = DayoTheme.typography.caption1.copy(color = Gray2_767B83),
                text = if (isLatest) "최신순" else "오래된순"
            )
        }
    }
}