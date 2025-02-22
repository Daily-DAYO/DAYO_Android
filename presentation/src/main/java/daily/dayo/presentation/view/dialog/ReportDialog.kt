package daily.dayo.presentation.view.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.DayoTheme

@Composable
fun CommentReportDialog(onClickCancel: () -> Unit, onClickConfirm: (String) -> Unit) {
    val reportCommentReasons = stringArrayResource(id = R.array.report_comment_reasons)

    RadioButtonDialog(
        title = stringResource(id = R.string.report_comment_title),
        description = stringResource(id = R.string.report_comment_description),
        radioItems = reportCommentReasons,
        onClickCancel = onClickCancel,
        onClickConfirm = onClickConfirm,
        modifier = Modifier
            .height(400.dp)
            .imePadding()
            .clip(RoundedCornerShape(28.dp))
            .background(DayoTheme.colorScheme.background)
    )
}

@Composable
fun PostReportDialog(onClickCancel: () -> Unit, onClickConfirm: (String) -> Unit) {
    val reportPostReasons = stringArrayResource(id = R.array.report_post_reasons)

    RadioButtonDialog(
        title = stringResource(id = R.string.report_post_title),
        description = stringResource(id = R.string.report_post_description),
        radioItems = reportPostReasons,
        lastInputEnabled = true,
        lastTextPlaceholder = stringResource(id = R.string.report_post_reason_other_hint),
        lastTextMaxLength = 100,
        onClickCancel = onClickCancel,
        onClickConfirm = onClickConfirm,
        modifier = Modifier
            .height(400.dp)
            .imePadding()
            .clip(RoundedCornerShape(28.dp))
            .background(DayoTheme.colorScheme.background)
    )
}
