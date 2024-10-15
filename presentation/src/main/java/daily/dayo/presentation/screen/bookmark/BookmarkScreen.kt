package daily.dayo.presentation.screen.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.BookmarkPost
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.caption2
import daily.dayo.presentation.theme.caption4
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.ProfileViewModel
import java.text.DecimalFormat

@Composable
fun BookmarkScreen(
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val bookmarkPosts = profileViewModel.bookmarkPostList.collectAsLazyPagingItems()

    LaunchedEffect(true) {
        profileViewModel.requestAllMyBookmarkPostList()
    }

    Scaffold(
        topBar = { BookmarkTopNavigation(onBackClick) },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .background(color = White_FFFFFF)
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                BookmarkHeader(bookmarkPosts.itemCount)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 12.dp, start = 18.dp, end = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (bookmarkPosts.loadState.refresh) {
                        is LoadState.Loading -> {}
                        is LoadState.Error -> {}
                        is LoadState.NotLoading -> {
                            items(bookmarkPosts.itemCount) { index ->
                                bookmarkPosts[index]?.let { post ->
                                    BookmarkPostItem(post)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun BookmarkTopNavigation(
    onBackClick: () -> Unit
) {
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
                    contentDescription = "back sign",
                    tint = Gray1_50545B
                )
            }
        },
        title = stringResource(id = R.string.bookmark),
        titleAlignment = TopNavigationAlign.CENTER
    )
}

@Composable
private fun BookmarkHeader(postCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 18.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            val dec = DecimalFormat("#,###")
            Text(
                text = " ${dec.format(postCount)} ",
                style = MaterialTheme.typography.caption2.copy(Primary_23C882),
            )
            Text(
                text = stringResource(id = R.string.bookmark_count),
                style = MaterialTheme.typography.caption2.copy(Gray2_767B83)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                text = stringResource(id = R.string.bookmark_edit),
                style = MaterialTheme.typography.caption4.copy(color = Gray1_50545B),
            )
        }
    }
}

@Composable
private fun BookmarkPostItem(post: BookmarkPost) {
    RoundImageView(
        context = LocalContext.current,
        imageUrl = "${BuildConfig.BASE_URL}/images/${post.thumbnailImage}",
        imageDescription = "bookmark post thumbnail",
        customModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    )
}

@Preview
@Composable
private fun PreviewBookmarkHeader() {
    BookmarkHeader(0)
}