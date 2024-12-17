package daily.dayo.presentation.screen.write

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.domain.model.Privacy
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.limitTo
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.DayoTextField
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.WriteViewModel

const val WRITE_FOLDER_NEW_FOLDER_NAME_MAX_LENGTH = 12
const val WRITE_FOLDER_NEW_FOLDER_DESCRIPTION_MAX_LENGTH = 20

@Composable
fun WriteFolderNewRoute(
    onBackClick: () -> Unit,
    writeViewModel: WriteViewModel = hiltViewModel(),
) {
    val folderAddSuccess by writeViewModel.writeFolderAddSuccess.collectAsStateWithLifecycle()
    LaunchedEffect(folderAddSuccess) {
        if (folderAddSuccess.getContentIfNotHandled() == true) {
            onBackClick()
        }
    }


    WriteFolderNewScreen(
        onBackClick = onBackClick,
        writeViewModel = writeViewModel,
    )
}

@Composable
fun WriteFolderNewScreen(
    onBackClick: () -> Unit,
    writeViewModel: WriteViewModel,
) {
    val newFolderTitle = rememberSaveable { mutableStateOf("") }
    val newFolderDescription = rememberSaveable { mutableStateOf("") }
    var isToggled = rememberSaveable { mutableStateOf(false) }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            WriteFolderNewActionBarLayout(
                onBackClick = onBackClick,
                onCreateNewFolderClick = {
                    writeViewModel.requestCreateFolderInPost(
                        newFolderTitle.value.limitTo(WRITE_FOLDER_NEW_FOLDER_NAME_MAX_LENGTH),
                        newFolderDescription.value.limitTo(
                            WRITE_FOLDER_NEW_FOLDER_DESCRIPTION_MAX_LENGTH
                        ),
                        if (isToggled.value) Privacy.ONLY_ME else Privacy.ALL
                    )
                },
            )
            WriteFolderNewContent(newFolderTitle, newFolderDescription, isToggled)
        }
    }
}

@Composable
fun WriteFolderNewActionBarLayout(
    onBackClick: () -> Unit,
    onCreateNewFolderClick: () -> Unit,
) {
    Column {
        TopNavigation(
            leftIcon = {
                NoRippleIconButton(
                    onClick = onBackClick,
                    iconContentDescription = "Previous Page",
                    iconPainter = painterResource(id = R.drawable.ic_arrow_left)
                )
            },
            title = stringResource(R.string.write_post_folder_new_title),
            rightIcon = {
                DayoTextButton(
                    onClick = { onCreateNewFolderClick() },
                    text = stringResource(R.string.confirm),
                    textStyle = DayoTheme.typography.b3.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(10.dp),
                )
            },
            titleAlignment = TopNavigationAlign.CENTER
        )
    }
}

@Preview
@Composable
fun WriteFolderNewContent(
    newFolderTitle: MutableState<String> = mutableStateOf(""),
    newFolderDescription: MutableState<String> = mutableStateOf(""),
    isToggled: MutableState<Boolean> = mutableStateOf(false),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(White_FFFFFF)
    ) {
        DayoTextField(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            value = newFolderTitle.value,
            onValueChange = { textValue ->
                if (textValue.length > WRITE_FOLDER_NEW_FOLDER_NAME_MAX_LENGTH)
                    return@DayoTextField

                newFolderTitle.value = textValue
            },
            label = stringResource(R.string.write_post_folder_new_folder_name_title),
            placeholder = stringResource(R.string.write_post_folder_new_folder_name_placeholder)
        )
        Spacer(modifier = Modifier.height(28.dp))
        DayoTextField(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            value = newFolderDescription.value,
            onValueChange = { textValue ->
                if (textValue.length > WRITE_FOLDER_NEW_FOLDER_DESCRIPTION_MAX_LENGTH)
                    return@DayoTextField

                newFolderDescription.value = textValue
            },
            label = stringResource(R.string.write_post_folder_new_folder_description_title),
            placeholder = stringResource(R.string.write_post_folder_new_folder_description_placeholder)
        )
        Spacer(modifier = Modifier.height(40.dp))
        ToggleButtonWithLabel(
            isToggled = isToggled.value,
            onToggleChanged = { isToggled.value = it }
        )
    }
}

@Composable
fun ToggleButtonWithLabel(
    isToggled: Boolean,
    onToggleChanged: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .padding(18.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = stringResource(R.string.write_post_folder_new_folder_privacy_title),
            style = DayoTheme.typography.b6.copy(color = Gray3_9FA5AE),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isToggled,
            onCheckedChange = { onToggleChanged(it) },
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = White_FFFFFF,
                checkedThumbColor = White_FFFFFF,
                checkedTrackColor = Primary_23C882,
                uncheckedTrackColor = Gray6_F0F1F3,
                uncheckedBorderColor = Gray6_F0F1F3,
                checkedBorderColor = Primary_23C882,
            )
        )
    }
}
