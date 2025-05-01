package daily.dayo.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Red_FF4545

@Composable
fun MyPostDropdownMenu(postId: Long, expanded: Boolean, onDismissRequest: () -> Unit, onPostModifyClick: (Long) -> Unit, onPostDeleteClick: (Long) -> Unit) {
    DayoTheme(shapes = DayoTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(DayoTheme.colorScheme.background)
        ) {
            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clip(RoundedCornerShape(12.dp)),
                text = {
                    Row(
                        modifier = Modifier.width(128.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_post),
                            contentDescription = stringResource(R.string.post_option_mine_modify),
                            tint = Dark,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.post_option_mine_modify),
                            style = DayoTheme.typography.b6.copy(Dark)
                        )
                    }
                },
                onClick = {
                    onDismissRequest()
                    onPostModifyClick(postId)
                }
            )

            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clip(RoundedCornerShape(12.dp)),
                text = {
                    Row(
                        modifier = Modifier.width(128.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_delete),
                            contentDescription = stringResource(R.string.post_option_mine_delete),
                            tint = Red_FF4545,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.post_option_mine_delete),
                            style = DayoTheme.typography.b6.copy(Red_FF4545)
                        )
                    }
                },
                onClick = {
                    onDismissRequest()
                    onPostDeleteClick(postId)
                }
            )
        }
    }
}

@Composable
fun OthersPostDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, onPostReportClick: () -> Unit) {
    DayoTheme(shapes = DayoTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(DayoTheme.colorScheme.background)
        ) {
            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clip(RoundedCornerShape(12.dp)),
                text = {
                    Row(
                        modifier = Modifier.width(128.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_report),
                            contentDescription = stringResource(R.string.post_option_report_post),
                            tint = Dark,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.post_option_report_post),
                            style = DayoTheme.typography.b6.copy(Dark)
                        )
                    }
                },
                onClick = onPostReportClick
            )
        }
    }
}

@Composable
fun ProfileDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, onUserReportClick: () -> Unit) {
    DayoTheme(shapes = DayoTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(DayoTheme.colorScheme.background)
        ) {
            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clip(RoundedCornerShape(12.dp)),
                text = {
                    Row(
                        modifier = Modifier.width(128.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_report),
                            contentDescription = stringResource(R.string.other_profile_option_report_user),
                            tint = Dark,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.other_profile_option_report_user),
                            style = DayoTheme.typography.b6.copy(Dark)
                        )
                    }
                },
                onClick = onUserReportClick
            )
        }
    }
}
