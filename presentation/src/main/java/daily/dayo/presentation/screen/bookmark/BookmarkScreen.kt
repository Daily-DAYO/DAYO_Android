package daily.dayo.presentation.screen.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.BookmarkPost
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.DayoCheckbox
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.BookmarkViewModel
import java.text.DecimalFormat

@Composable
fun BookmarkScreen(
    onBackClick: () -> Unit,
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarkUiState by bookmarkViewModel.uiState.collectAsStateWithLifecycle()
    val bookmarkPosts = bookmarkUiState.bookmarks.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            BookmarkTopNavigation(
                isEditMode = bookmarkUiState.isEditMode,
                onBackClick = onBackClick,
                onCancelClick = { bookmarkViewModel.toggleEditMode() }
            )
        },
        bottomBar = {
            if (bookmarkUiState.isEditMode) {
                FilledRoundedCornerButton(
                    onClick = {
                        bookmarkViewModel.deleteSelectedBookmarks()
                    },
                    label = stringResource(id = R.string.delete),
                    enabled = bookmarkUiState.selectedBookmarks.isNotEmpty(),
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(44.dp)
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .background(DayoTheme.colorScheme.background)
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            BookmarkHeader(
                bookmarkCount = bookmarkUiState.count,
                isEditMode = bookmarkUiState.isEditMode,
                selectedCount = bookmarkUiState.selectedBookmarks.size,
                onEditClick = { bookmarkViewModel.toggleEditMode() }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 12.dp, start = 18.dp, end = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookmarkPosts.itemCount) { index ->
                    bookmarkPosts[index]?.let { post ->
                        BookmarkPostItem(
                            post = post,
                            isEditMode = bookmarkUiState.isEditMode,
                            isSelected = bookmarkUiState.selectedBookmarks.contains(post.postId),
                            onBookmarkClick = { bookmarkViewModel.toggleSelection(post.postId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkTopNavigation(
    isEditMode: Boolean,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    if (isEditMode) {
        TopNavigation(
            leftIcon = {
                Text(
                    modifier = Modifier
                        .padding(vertical = 14.dp)
                        .padding(start = 18.dp, end = 27.dp)
                        .clickableSingle(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCancelClick
                        ),
                    text = stringResource(id = R.string.cancel),
                    style = DayoTheme.typography.b6.copy(color = Gray1_50545B),
                )
            }
        )
    } else {
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
                        painter = painterResource(id = R.drawable.ic_arrow_left_2),
                        contentDescription = "back sign",
                        tint = Gray1_50545B
                    )
                }
            },
            title = stringResource(id = R.string.bookmark),
            titleAlignment = TopNavigationAlign.CENTER
        )
    }
}

@Composable
private fun BookmarkHeader(
    bookmarkCount: Int,
    selectedCount: Int,
    isEditMode: Boolean,
    onEditClick: () -> Unit
) {
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
                text = " ${dec.format(bookmarkCount)} ",
                style = DayoTheme.typography.caption2.copy(Primary_23C882),
            )
            Text(
                text = stringResource(id = R.string.bookmark_count),
                style = DayoTheme.typography.caption2.copy(Gray2_767B83)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (isEditMode) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    text = stringResource(id = R.string.bookmark_selected_count, selectedCount),
                    style = DayoTheme.typography.caption4.copy(color = Gray2_767B83),
                )
            } else {
                Text(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .clickableSingle(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onEditClick
                        ),
                    text = stringResource(id = R.string.bookmark_edit),
                    style = DayoTheme.typography.caption4.copy(color = Gray2_767B83),
                )
            }
        }
    }
}

@Composable
private fun BookmarkPostItem(
    post: BookmarkPost,
    isEditMode: Boolean,
    isSelected: Boolean,
    onBookmarkClick: () -> Unit
) {
    Box {
        RoundImageView(
            context = LocalContext.current,
            imageUrl = "${BuildConfig.BASE_URL}/images/${post.thumbnailImage}",
            imageDescription = "bookmark post thumbnail",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        if (isEditMode) {
            DayoCheckbox(
                checked = isSelected,
                onCheckedChange = { onBookmarkClick() },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBookmarkHeader() {
    BookmarkHeader(0, 0, true, {})
}