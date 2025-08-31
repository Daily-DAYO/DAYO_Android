package daily.dayo.presentation.screen.folder

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import daily.dayo.domain.model.Folder
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.constant.FolderConstants.MAX_FOLDER_COUNT
import daily.dayo.presentation.screen.write.WriteFolderScreen
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.viewmodel.FolderViewModel

@Composable
internal fun FolderPostMoveScreen(
    currentFolderId: Long,
    navigateToCreateNewFolder: () -> Unit,
    navigateBackToFolder: () -> Unit,
    onAdRequest: (onRewardSuccess: () -> Unit) -> Unit,
    onBackClick: () -> Unit,
    folderViewModel: FolderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val folderListState = folderViewModel.folderList.observeAsState()
    val folderList = when (folderListState.value?.status) {
        Status.SUCCESS -> folderListState.value?.data ?: emptyList()
        else -> emptyList()
    }
    var selectedFolder by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        folderViewModel.requestAllMyFolderList()
    }

    LaunchedEffect(Unit) {
        folderViewModel.postMoveSuccess
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { postMoveSuccess ->
                if (postMoveSuccess) {
                    navigateBackToFolder()
                } else {
                    Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }
    }

    FolderPostMoveScreen(
        currentFolderId = currentFolderId,
        folders = folderList,
        selectedFolder = selectedFolder,
        onFolderClick = { folderId, _ ->
            selectedFolder = folderId
        },
        onPostMoveClick = {
            selectedFolder?.let { folderViewModel.moveSelectedPost(it) }
        },
        navigateToCreateNewFolder = navigateToCreateNewFolder,
        onAdRequest = onAdRequest,
        onBackClick = onBackClick
    )
}

@Composable
private fun FolderPostMoveScreen(
    currentFolderId: Long,
    folders: List<Folder>,
    selectedFolder: Long?,
    onFolderClick: (Long, String) -> Unit,
    onPostMoveClick: () -> Unit,
    navigateToCreateNewFolder: () -> Unit,
    onAdRequest: (onRewardSuccess: () -> Unit) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(20.dp)) {
                FilledRoundedCornerButton(
                    label = stringResource(id = R.string.folder_post_move),
                    onClick = onPostMoveClick,
                    modifier = Modifier.height(44.dp),
                    enabled = selectedFolder != null,
                    textStyle = DayoTheme.typography.b5
                )
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            WriteFolderScreen(
                showCreateFolder = folders.size < MAX_FOLDER_COUNT,
                onBackClick = onBackClick,
                onFolderClick = onFolderClick,
                navigateToCreateNewFolder = navigateToCreateNewFolder,
                folders = folders.filterNot { it.folderId == currentFolderId },
                currentFolderId = selectedFolder,
                onAdRequest = onAdRequest
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFolderPostMoveScreen() {
    FolderPostMoveScreen(
        currentFolderId = 0,
        folders = emptyList(),
        selectedFolder = null,
        onFolderClick = { folderId, folderName -> },
        onPostMoveClick = {},
        navigateToCreateNewFolder = {},
        onAdRequest = {},
        onBackClick = {}
    )
}
