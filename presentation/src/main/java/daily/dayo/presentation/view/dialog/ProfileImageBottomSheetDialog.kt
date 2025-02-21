package daily.dayo.presentation.view.dialog

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import daily.dayo.presentation.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileImageBottomSheetDialog(
    bottomSheetState: ModalBottomSheetState,
    onClickProfileSelect: () -> Unit,
    onClickProfileCapture: () -> Unit,
    onClickProfileReset: () -> Unit,
) {
    BottomSheetDialog(
        sheetState = bottomSheetState,
        buttons = listOf(
            Pair(stringResource(id = R.string.image_option_gallery)) {
                onClickProfileSelect()
            }, Pair(stringResource(id = R.string.image_option_camera)) {
                onClickProfileCapture()
            }, Pair(stringResource(id = R.string.sign_up_email_set_profile_image_reset)) {
                onClickProfileReset()
            }),
        isFirstButtonColored = true
    )
}