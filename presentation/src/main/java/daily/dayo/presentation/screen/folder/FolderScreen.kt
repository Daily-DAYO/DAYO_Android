package daily.dayo.presentation.screen.folder

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.domain.model.FolderInfo
import daily.dayo.domain.model.FolderOrder
import daily.dayo.domain.model.FolderPost
import daily.dayo.domain.model.Privacy
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.createLoadingDialog
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.hideLoadingDialog
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.resizeDialogFragment
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.showLoadingDialog
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.Red_FF4545
import daily.dayo.presentation.view.DayoCheckbox
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.ConfirmDialog
import daily.dayo.presentation.viewmodel.FolderUiState
import daily.dayo.presentation.viewmodel.FolderViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun FolderScreen(
    folderId: Long,
    onPostClick: (Long) -> Unit,
    onFolderEditClick: () -> Unit,
    onPostMoveClick: () -> Unit,
    onWritePostWithFolderClick: () -> Unit,
    onBackClick: () -> Unit,
    folderViewModel: FolderViewModel = hiltViewModel()
) {
    val folderUiState by folderViewModel.uiState.collectAsStateWithLifecycle()
    val folderPosts = folderUiState.folderPosts.collectAsLazyPagingItems()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val loadingAlertDialog = remember { mutableStateOf(createLoadingDialog(context)) }
    val folderDeleteSuccess by folderViewModel.folderDeleteSuccess.collectAsStateWithLifecycle(false)
    var showFolderDeleteAlertDialog by remember { mutableStateOf(false) }
    var showPostDeleteAlertDialog by remember { mutableStateOf(false) }

    val optionMenu = listOf(
        FolderOptionMenu(
            name = stringResource(id = R.string.folder_option_post_edit),
            iconRes = R.drawable.ic_menu_post,
            color = Dark,
            onClickMenu = {
                folderViewModel.toggleEditMode()
            }
        ),
        FolderOptionMenu(
            name = stringResource(id = R.string.folder_option_edit),
            iconRes = R.drawable.ic_menu_folder,
            color = Dark,
            onClickMenu = onFolderEditClick
        ),
        FolderOptionMenu(
            name = stringResource(id = R.string.folder_option_delete),
            iconRes = R.drawable.ic_menu_delete,
            color = Red_FF4545,
            onClickMenu = {
                showFolderDeleteAlertDialog = true
            }
        )
    )

    LaunchedEffect(folderId) {
        folderViewModel.requestFolderInfo(folderId)
        folderViewModel.requestFolderPostList(folderId)
    }

    LaunchedEffect(folderDeleteSuccess) {
        if (folderDeleteSuccess) {
            hideLoadingDialog(loadingAlertDialog.value)
            onBackClick()
        }
    }

    LaunchedEffect(Unit) {
        folderViewModel.postDeleteSuccess
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { postDeleteSuccess ->
                if (!postDeleteSuccess) {
                    Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
                folderViewModel.toggleEditMode()
                folderViewModel.requestFolderPostList(folderId)
            }
    }

    FolderScreen(
        folderUiState = folderUiState,
        folderPosts = folderPosts,
        optionMenu = optionMenu,
        onPostClick = { postId -> onPostClick(postId) },
        onPostSelect = { postId -> folderViewModel.toggleSelection(postId) },
        onWritePostWithFolderClick = onWritePostWithFolderClick,
        onCancelClick = { folderViewModel.toggleEditMode() },
        onPostDeleteClick = { showPostDeleteAlertDialog = true },
        onClickSort = { folderViewModel.toggleFolderOrder(folderId) },
        onPostMoveClick = onPostMoveClick,
        onBackClick = onBackClick
    )

    if (showFolderDeleteAlertDialog) {
        FolderDeleteAlertDialog(
            folderName = folderUiState.folderInfo.name,
            onClickConfirm = {
                showFolderDeleteAlertDialog = false
                showLoadingDialog(loadingAlertDialog.value)
                resizeDialogFragment(context, loadingAlertDialog.value, 0.8f)
                folderViewModel.requestDeleteFolder(folderId)
            },
            onShowChange = { showFolderDeleteAlertDialog = it }
        )
    }

    if (showPostDeleteAlertDialog) {
        PostDeleteAlertDialog(
            selectedCount = folderUiState.selectedPosts.size,
            onClickConfirm = {
                showPostDeleteAlertDialog = false
                // TODO Show Loading
                folderViewModel.deletePosts()
                folderViewModel.requestDeleteFolder(folderId)
            },
            onShowChange = { showPostDeleteAlertDialog = it }
        )
    }
}

@Composable
private fun FolderScreen(
    folderUiState: FolderUiState,
    folderPosts: LazyPagingItems<FolderPost>,
    optionMenu: List<FolderOptionMenu>,
    onPostClick: (Long) -> Unit,
    onPostSelect: (Long) -> Unit,
    onWritePostWithFolderClick: () -> Unit,
    onPostDeleteClick: () -> Unit,
    onPostMoveClick: () -> Unit,
    onClickSort: () -> Unit,
    onCancelClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val optionExpanded = remember { mutableStateOf(false) }

    with(folderUiState) {
        Scaffold(
            topBar = {
                if (isEditMode) {
                    TopNavigation(
                        leftIcon = {
                            androidx.compose.material.Text(
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
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back_sign),
                                    contentDescription = stringResource(id = R.string.back_sign),
                                    tint = Dark
                                )
                            }
                        },
                        rightIcon = {
                            IconButton(
                                onClick = { optionExpanded.value = optionExpanded.value.not() }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_option_horizontal),
                                    contentDescription = stringResource(id = R.string.folder_option),
                                    tint = Dark
                                )
                            }

                            FolderDropdownMenu(
                                menuItems = optionMenu,
                                expanded = optionExpanded
                            )
                        }
                    )
                }
            },
            bottomBar = {
                if (isEditMode) {
                    Row(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        FilledRoundedCornerButton(
                            label = stringResource(R.string.delete),
                            onClick = onPostDeleteClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            enabled = selectedPosts.isNotEmpty(),
                            color = ButtonDefaults.buttonColors(
                                containerColor = Gray5_E8EAEE,
                                contentColor = Gray2_767B83,
                                disabledContainerColor = Gray7_F6F6F7,
                                disabledContentColor = Gray4_C5CAD2
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        FilledRoundedCornerButton(
                            label = stringResource(R.string.move),
                            onClick = onPostMoveClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            enabled = selectedPosts.isNotEmpty()
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (!isEditMode) {
                    FolderInformation(folderInfo, onWritePostWithFolderClick)
                }
                FolderHeader(
                    postCount = folderInfo.postCount,
                    selectedCount = selectedPosts.size,
                    isEditMode = isEditMode,
                    folderOrder = folderOrder,
                    onClickSort = onClickSort
                )
                FolderContent(
                    folderUiState = folderUiState,
                    folderPosts = folderPosts,
                    onPostClick = onPostClick,
                    onPostSelect = onPostSelect
                )
            }
        }
    }
}

@Composable
private fun FolderInformation(folderInfo: FolderInfo, onWritePostWithFolderClick: () -> Unit) {
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
            text = folderInfo.subheading,
            color = Gray1_50545B,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = DayoTheme.typography.b6
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onWritePostWithFolderClick,
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
private fun FolderDropdownMenu(
    menuItems: List<FolderOptionMenu>,
    expanded: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .width(140.dp)
    ) {
        menuItems.forEach {
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = it.iconRes),
                            contentDescription = it.name,
                            tint = it.color
                        )

                        Text(
                            text = it.name,
                            color = it.color,
                            style = DayoTheme.typography.b6
                        )
                    }
                },
                onClick = {
                    it.onClickMenu()
                    expanded.value = false
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.5.dp)
            )
        }
    }
}

@Composable
private fun FolderHeader(
    postCount: Int,
    selectedCount: Int,
    isEditMode: Boolean,
    folderOrder: FolderOrder,
    onClickSort: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$postCount",
            color = Primary_23C882,
            style = DayoTheme.typography.caption2
        )

        Text(
            text = " ${stringResource(id = R.string.folder_post_count_description)}",
            color = Gray2_767B83,
            style = DayoTheme.typography.caption2
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isEditMode) {
            Text(
                text = stringResource(id = R.string.folder_post_selected_count, selectedCount),
                color = Gray2_767B83,
                style = DayoTheme.typography.caption2
            )
        } else {
            FolderSortSelector(folderOrder = folderOrder, onClickSort = onClickSort)
        }
    }
}

@Composable
private fun FolderSortSelector(folderOrder: FolderOrder, onClickSort: () -> Unit) {
    val sortResId = when (folderOrder) {
        FolderOrder.NEW -> R.string.folder_post_sort_newest
        FolderOrder.OLD -> R.string.folder_post_sort_oldest
    }

    Row(
        modifier = Modifier.clickableSingle { onClickSort() }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sort),
            contentDescription = stringResource(id = sortResId),
            tint = Color.Unspecified
        )

        Text(
            text = stringResource(id = sortResId),
            color = Gray2_767B83,
            style = DayoTheme.typography.caption2
        )
    }
}

@Composable
private fun FolderContent(
    folderUiState: FolderUiState,
    folderPosts: LazyPagingItems<FolderPost>,
    onPostClick: (Long) -> Unit,
    onPostSelect: (Long) -> Unit
) {
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
                    onPostClick = onPostClick,
                    onPostSelect = onPostSelect
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
    onPostClick: (Long) -> Unit,
    onPostSelect: (Long) -> Unit
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                if (isEditMode) {
                    onPostSelect(post.postId)
                } else {
                    onPostClick(post.postId)
                }
            }
        )
    ) {
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
                onCheckedChange = { onPostSelect(post.postId) },
                modifier = Modifier.align(Alignment.TopEnd),
                interactionSource = interactionSource
            )
        }
    }
}

@Composable
private fun FolderDeleteAlertDialog(
    folderName: String,
    onClickConfirm: () -> Unit,
    onShowChange: (Boolean) -> Unit
) {
    val folderDeleteDescription = stringResource(
        R.string.folder_delete_description_message,
        folderName
    )

    val folderDeleteExplanation = stringResource(
        R.string.folder_delete_explanation_message,
        folderName
    )

    ConfirmDialog(
        title = folderDeleteDescription,
        description = folderDeleteExplanation,
        onClickConfirm = onClickConfirm,
        onClickCancel = { onShowChange(false) }
    )
}

@Composable
private fun PostDeleteAlertDialog(
    selectedCount: Int,
    onClickConfirm: () -> Unit,
    onShowChange: (Boolean) -> Unit
) {
    val postDeleteDescription = stringResource(
        R.string.folder_post_delete_description_message,
        selectedCount
    )

    val postDeleteExplanation = stringResource(R.string.folder_post_delete_explanation_message)

    ConfirmDialog(
        title = postDeleteDescription,
        description = postDeleteExplanation,
        onClickConfirm = onClickConfirm,
        onClickCancel = { onShowChange(false) }
    )
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
            optionMenu = listOf(),
            onPostClick = { },
            onPostSelect = { },
            onWritePostWithFolderClick = { },
            onPostDeleteClick = { },
            onPostMoveClick = { },
            onClickSort = { },
            onCancelClick = { },
            onBackClick = { }
        )
    }
}

@Preview
@Composable
private fun PreviewFolderScreenEditMode() {
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
        isEditMode = true,
        selectedPosts = emptySet()
    )

    DayoTheme {
        FolderScreen(
            folderUiState = folderUiState,
            folderPosts = folderUiState.folderPosts.collectAsLazyPagingItems(),
            optionMenu = listOf(),
            onPostClick = { },
            onPostSelect = { },
            onWritePostWithFolderClick = { },
            onPostDeleteClick = { },
            onPostMoveClick = { },
            onClickSort = { },
            onCancelClick = { },
            onBackClick = { }
        )
    }
}

data class FolderOptionMenu(
    val name: String,
    @DrawableRes val iconRes: Int,
    val color: Color,
    val onClickMenu: () -> Unit
)
