package daily.dayo.presentation.view.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import daily.dayo.presentation.R

@Composable
fun NetworkErrorDialog(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.network_error_dialog_default_message),
    description: String = "",
    onClickRetry: () -> Unit = {},
    onClickRetryText: String = stringResource(id = R.string.re_try),
) {
    ConfirmDialog(
        title = title,
        description = description,
        onClickConfirm = onClickRetry,
        modifier = modifier,
        onClickCancel = null,
        onClickConfirmText = onClickRetryText
    )
}
