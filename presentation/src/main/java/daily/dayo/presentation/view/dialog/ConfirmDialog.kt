package daily.dayo.presentation.view.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.DayoTextButton

@Composable
fun ConfirmDialog(
    title: String,
    description: String,
    onClickConfirm: () -> Unit, // onClickConfirm is required
    modifier: Modifier = Modifier,
    onClickCancel: (() -> Unit)? = {},
    onClickConfirmText: String = stringResource(id = R.string.confirm),
    onClickCancelText: String = stringResource(id = R.string.cancel),
) {
    Dialog(
        onDismissRequest = onClickCancel ?: onClickConfirm,
    ) {
        Surface(
            modifier = modifier
                .background(
                    DayoTheme.colorScheme.background,
                    RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp))
        ) {
            Column {
                DialogHeader(title, description)
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Gray7_F6F6F7
                )
                DialogActionButton(
                    onClickConfirmText = onClickConfirmText,
                    onClickConfirm = onClickConfirm,
                    onClickCancelText = onClickCancelText,
                    onClickCancel = onClickCancel
                )
            }
        }
    }
}

@Composable
private fun DialogHeader(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                color = Dark,
                textAlign = TextAlign.Center,
                style = DayoTheme.typography.b3
            )
        }

        if (description.isNotBlank()) {
            Text(
                text = description,
                color = Gray2_767B83,
                textAlign = TextAlign.Center,
                style = DayoTheme.typography.caption4
            )
        }
    }
}

@Composable
private fun DialogActionButton(
    onClickConfirm: () -> Unit,
    onClickCancel: (() -> Unit)? = {},
    onClickConfirmText: String = stringResource(id = R.string.confirm),
    onClickCancelText: String = stringResource(id = R.string.cancel),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onClickCancel != null) {
            DayoTextButton(
                text = onClickCancelText,
                onClick = onClickCancel,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                textStyle = DayoTheme.typography.b5.copy(Gray3_9FA5AE)
            )
        }

        DayoTextButton(
            text = onClickConfirmText,
            onClick = onClickConfirm,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            textStyle = DayoTheme.typography.b5.copy(Primary_23C882)
        )
    }
}

@Preview
@Composable
private fun PreviewConfirmDialog() {
    DayoTheme {
        ConfirmDialog("title", "description",
            onClickConfirm = {},
            onClickCancel = {}
        )
    }
}
