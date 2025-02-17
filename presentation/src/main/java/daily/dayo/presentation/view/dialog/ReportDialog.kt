package daily.dayo.presentation.view.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.DayoTheme

@Composable
fun CommentReportDialog(onClickClose: () -> Unit, onClickConfirm: (String) -> Unit) {
    val reportReasons = arrayListOf(
        stringResource(id = R.string.report_comment_reason_1),
        stringResource(id = R.string.report_comment_reason_2),
        stringResource(id = R.string.report_comment_reason_3),
        stringResource(id = R.string.report_comment_reason_4),
        stringResource(id = R.string.report_comment_reason_5),
        stringResource(id = R.string.report_comment_reason_6),
        stringResource(id = R.string.report_comment_reason_7)
    )

    RadioButtonDialog(
        title = stringResource(id = R.string.report_comment_title),
        description = stringResource(id = R.string.report_comment_description),
        radioItems = reportReasons,
        onClickCancel = onClickClose,
        onClickConfirm = onClickConfirm,
        modifier = Modifier
            .height(400.dp)
            .imePadding()
            .clip(RoundedCornerShape(28.dp))
            .background(DayoTheme.colorScheme.background)
    )
}
