package daily.dayo.presentation.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.caption2

@Composable
fun Chip(
    onClickChip: (() -> Unit) = {},
    text: String,
    color: Color = PrimaryGreen_23C882,
    enabled: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClickChip,
        enabled = enabled,
        label = {
            Text(
                text = text,
                color = if (enabled) color else Gray5_E8EAEE,
                style = MaterialTheme.typography.caption2
            )
        },
        shape = RoundedCornerShape(100.dp),
        border = AssistChipDefaults.assistChipBorder(
            borderColor = color,
            borderWidth = 1.dp,
            disabledBorderColor = Gray6_F0F1F3
        ),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = White_FFFFFF,
            disabledContainerColor = White_FFFFFF
        ),
        modifier = modifier
    )
}

@Preview
@Composable
fun PreviewChip() {
    MaterialTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Chip(text = "Text")
            Chip(text = "Text", enabled = false)
            Chip(text = "Text", color = Gray1_313131)
        }
    }
}