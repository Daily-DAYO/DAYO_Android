package daily.dayo.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.White_FFFFFF

@Composable
fun DayoCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "checkbox"
) {
    IconButton(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_check_ok_sign),
            tint = if (checked) Color.Unspecified else White_FFFFFF.copy(0.4f),
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = if (checked) Color.Transparent else White_FFFFFF,
                    shape = RoundedCornerShape(size = 100.dp)
                )
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                ),
            contentDescription = contentDescription
        )
    }
}

@Preview
@Composable
private fun PreviewDayoCheckbox() {
    Row {
        DayoCheckbox(true, {})
        DayoCheckbox(false, {})
    }
}
