package daily.dayo.presentation.screen.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Folder
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.screen.write.WriteFolderScreen
import daily.dayo.presentation.viewmodel.FolderViewModel

@Composable
internal fun FolderPostMoveScreen(
    onBackClick: () -> Unit,
    folderViewModel: FolderViewModel = hiltViewModel()
) {
    val folderList = folderViewModel.folderList.observeAsState()

    LaunchedEffect(Unit) {
        folderViewModel.requestAllMyFolderList()
    }

    FolderPostMoveScreen(
        folders = when (folderList.value?.status) {
            Status.SUCCESS -> folderList.value?.data ?: emptyList()
            else -> emptyList()
        },
        onFolderClick = { folderId, folderName ->

        },
        navigateToCreateNewFolder = {

        },
        onBackClick = onBackClick
    )
}

@Composable
private fun FolderPostMoveScreen(
    folders: List<Folder>,
    onFolderClick: (String, String) -> Unit,
    navigateToCreateNewFolder: () -> Unit,
    onBackClick: () -> Unit
) {
    WriteFolderScreen(
        onBackClick = onBackClick,
        onFolderClick = onFolderClick,
        navigateToCreateNewFolder = navigateToCreateNewFolder,
        folders = folders,
        currentFolderId = null,
    )
}

@Preview
@Composable
private fun PreviewFolderPostMoveScreen() {
    FolderPostMoveScreen(
        folders = emptyList(),
        onFolderClick = { folderId, folderName -> },
        navigateToCreateNewFolder = {},
        onBackClick = {}
    )
}
