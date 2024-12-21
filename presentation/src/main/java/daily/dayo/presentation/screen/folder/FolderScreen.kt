package daily.dayo.presentation.screen.folder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.FolderInfo
import daily.dayo.domain.model.FolderPost
import daily.dayo.domain.model.Privacy
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.DayoCheckbox
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.FolderUiState
import daily.dayo.presentation.viewmodel.FolderViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun FolderScreen(
    folderId: String,
    onBackClick: () -> Unit,
    folderViewModel: FolderViewModel = hiltViewModel()
) {
    val folderUiState by folderViewModel.uiState.collectAsStateWithLifecycle()
    val folderPosts = folderUiState.folderPosts.collectAsLazyPagingItems()

    LaunchedEffect(folderId) {
        folderViewModel.requestFolderInfo(folderId.toInt())
        folderViewModel.requestFolderPostList(folderId.toInt())
    }

    FolderScreen(
        folderUiState = folderUiState,
        folderPosts = folderPosts,
        onPostClick = { postId -> folderViewModel.toggleSelection(postId) },
        onBackClick = onBackClick
    )
}

@Composable
private fun FolderScreen(
    folderUiState: FolderUiState,
    folderPosts: LazyPagingItems<FolderPost>,
    onPostClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_sign),
                            contentDescription = stringResource(id = R.string.back_sign),
                            tint = Dark
                        )
                    }
                },
                rightIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_option_horizontal),
                            contentDescription = stringResource(id = R.string.folder_option),
                            tint = Dark
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            FolderInformation(folderUiState.folderInfo)
            FolderContent(
                folderUiState = folderUiState,
                folderPosts = folderPosts,
                onPostClick = onPostClick
            )
        }
    }
}

@Composable
private fun FolderInformation(folderInfo: FolderInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 16.dp)
    ) {
        Icon(
            painter = when (folderInfo.privacy) {
                Privacy.ALL -> painterResource(id = R.drawable.ic_folder_public_my_page)
                Privacy.ONLY_ME -> painterResource(id = R.drawable.ic_folder_private_my_page)
            },
            contentDescription = when (folderInfo.privacy) {
                Privacy.ALL -> stringResource(id = R.string.folder_privacy_public_icon_description)
                Privacy.ONLY_ME -> stringResource(id = R.string.folder_privacy_privacy_icon_description)
            },
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = folderInfo.name,
            color = Dark,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = DayoTheme.typography.h3
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = folderInfo.subheading ?: "",
            color = Gray1_50545B,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = DayoTheme.typography.b6
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gray6_F0F1F3,
                contentColor = Gray2_767B83
            ),
            contentPadding = PaddingValues(vertical = 9.5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.folder_post_add_icon_description)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.folder_post_add),
                color = Gray2_767B83,
                style = DayoTheme.typography.b5
            )
        }
    }
}

@Composable
private fun FolderContent(
    folderUiState: FolderUiState,
    folderPosts: LazyPagingItems<FolderPost>,
    onPostClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${folderUiState.folderInfo.postCount}",
            color = Primary_23C882,
            style = DayoTheme.typography.caption2
        )

        Text(
            text = " ${stringResource(id = R.string.folder_post_count_description)}",
            color = Gray2_767B83,
            style = DayoTheme.typography.caption2
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.ic_sort),
            contentDescription = stringResource(id = R.string.folder_post_sort_oldest),
            tint = Color.Unspecified
        )

        Text(
            text = stringResource(id = R.string.folder_post_sort_oldest),
            color = Gray2_767B83,
            style = DayoTheme.typography.caption2
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(folderPosts.itemCount) { index ->
            folderPosts[index]?.let { post ->
                FolderPostItem(
                    post = post,
                    isEditMode = folderUiState.isEditMode,
                    isSelected = folderUiState.selectedPosts.contains(post.postId),
                    onPostClick = onPostClick
                )
            }
        }
    }
}

@Composable
private fun FolderPostItem(
    post: FolderPost,
    isEditMode: Boolean,
    isSelected: Boolean,
    onPostClick: (Int) -> Unit
) {
    Box {
        RoundImageView(
            context = LocalContext.current,
            imageUrl = "${BuildConfig.BASE_URL}/images/${post.thumbnailImage}",
            imageDescription = "bookmark post thumbnail",
            customModifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        if (isEditMode) {
            DayoCheckbox(
                checked = isSelected,
                onCheckedChange = { onPostClick(post.postId) },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFolderScreen() {
    val folderPostPagingData = PagingData.from(
        listOf(
            FolderPost("", 0, "")
        )
    )

    val folderUiState = FolderUiState(
        folderInfo = FolderInfo(
            memberId = "",
            name = "Folder Title",
            postCount = 27,
            privacy = Privacy.ALL,
            subheading = "Description",
            thumbnailImage = ""
        ),
        folderPosts = flowOf(folderPostPagingData),
        isEditMode = false,
        selectedPosts = emptySet()
    )

    DayoTheme {
        FolderScreen(
            folderUiState = folderUiState,
            folderPosts = folderUiState.folderPosts.collectAsLazyPagingItems(),
            onPostClick = { },
            onBackClick = { }
        )
    }
}
