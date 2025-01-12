package daily.dayo.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF

@Composable
fun ToggleButtonWithLabel(
    label: String,
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
            text = label,
            color = Gray3_9FA5AE,
            style = DayoTheme.typography.b6
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
