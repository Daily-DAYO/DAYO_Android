package daily.dayo.presentation.screen.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.domain.model.FolderInfo
import daily.dayo.domain.model.Privacy
import daily.dayo.presentation.R
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.createLoadingDialog
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.hideLoadingDialog
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.resizeDialogFragment
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.showLoadingDialog
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.view.DayoTextField
import daily.dayo.presentation.view.ToggleButtonWithLabel
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.FOLDER_DESCRIPTION_MAX_LENGTH
import daily.dayo.presentation.viewmodel.FOLDER_NAME_MAX_LENGTH
import daily.dayo.presentation.viewmodel.FolderViewModel

@Composable
fun FolderEditScreen(
    folderId: Long,
    onBackClick: () -> Unit,
    folderViewModel: FolderViewModel = hiltViewModel()
) {
    val folderUiState by folderViewModel.uiState.collectAsStateWithLifecycle()
    val folderInfo = remember { mutableStateOf<FolderInfo?>(null) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val alertDialog = remember { mutableStateOf(createLoadingDialog(context)) }
    val editSuccess by folderViewModel.editSuccess.collectAsStateWithLifecycle(false)

    LaunchedEffect(folderId) {
        folderViewModel.requestFolderInfo(folderId)
    }

    LaunchedEffect(folderUiState) {
        folderInfo.value = folderUiState.folderInfo
    }

    LaunchedEffect(editSuccess) {
        if (editSuccess) {
            hideLoadingDialog(alertDialog.value)
            onBackClick()
        }
    }

    FolderEditScreen(
        folderInfo = folderInfo,
        confirmEnabled = (folderUiState.folderInfo != folderInfo.value) && (folderInfo.value?.name?.isNotEmpty() ?: false),
        onConfirmClick = {
            showLoadingDialog(alertDialog.value)
            resizeDialogFragment(context, alertDialog.value, 0.8f)

            folderInfo.value?.run {
                folderViewModel.requestEditFolder(
                    folderId = folderId,
                    name = name,
                    subheading = subheading,
                    privacy = privacy
                )
            }
            focusManager.clearFocus()
        },
        onBackClick = onBackClick
    )
}

@Composable
private fun FolderEditScreen(
    folderInfo: MutableState<FolderInfo?>,
    confirmEnabled: Boolean,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            FolderEditTopNavigation(
                confirmEnabled = confirmEnabled,
                onConfirmClick = onConfirmClick,
                onBackClick = onBackClick
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .background(DayoTheme.colorScheme.background)
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DayoTextField(
                value = folderInfo.value?.name ?: "",
                onValueChange = { textValue ->
                    if (textValue.length > FOLDER_NAME_MAX_LENGTH)
                        return@DayoTextField

                    folderInfo.value = folderInfo.value?.copy(
                        name = textValue
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.folder_setting_add_set_title),
                placeholder = stringResource(R.string.write_post_folder_new_folder_name_placeholder)
            )

            Spacer(modifier = Modifier.height(28.dp))

            DayoTextField(
                value = folderInfo.value?.subheading ?: "",
                onValueChange = { textValue ->
                    if (textValue.length > FOLDER_DESCRIPTION_MAX_LENGTH)
                        return@DayoTextField

                    folderInfo.value = folderInfo.value?.copy(
                        subheading = textValue
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.folder_setting_add_set_subheading),
                placeholder = stringResource(R.string.write_post_folder_new_folder_description_placeholder)
            )

            Spacer(modifier = Modifier.height(40.dp))

            ToggleButtonWithLabel(
                label = stringResource(R.string.write_post_folder_new_folder_privacy_title),
                isToggled = folderInfo.value?.privacy == Privacy.ONLY_ME,
                onToggleChanged = {
                    folderInfo.value = folderInfo.value?.copy(
                        privacy = if (it) Privacy.ONLY_ME else Privacy.ALL
                    )
                }
            )
        }
    }
}

@Composable
private fun FolderEditTopNavigation(
    confirmEnabled: Boolean,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    TopNavigation(
        title = stringResource(id = R.string.folder_edit_title),
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
            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .padding(start = 24.dp, end = 18.dp)
                    .clickableSingle(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = confirmEnabled,
                        onClick = onConfirmClick
                    ),
                text = stringResource(id = R.string.confirm),
                color = if (confirmEnabled) Dark else Gray5_E8EAEE,
                style = DayoTheme.typography.b3,
            )
        },
        titleAlignment = TopNavigationAlign.CENTER
    )
}

@Preview
@Composable
private fun PreviewFolderEditScreen() {
    DayoTheme {
        FolderEditScreen(
            folderInfo = remember {
                mutableStateOf(
                    FolderInfo(
                        memberId = "",
                        name = "Folder Title",
                        postCount = 27,
                        privacy = Privacy.ALL,
                        subheading = "Description",
                        thumbnailImage = ""
                    )
                )
            },
            confirmEnabled = false,
            onConfirmClick = {},
            onBackClick = {}
        )
    }
}
