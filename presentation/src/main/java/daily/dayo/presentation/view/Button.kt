package daily.dayo.presentation.view

import android.util.Size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b5

@Composable
fun FilledButton(
    onClick: () -> Unit,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isTonal: Boolean = false,
    isRoundedCorner: Boolean = false,
    size: Size? = null
) {
    val buttonColors = if (isTonal)
        ButtonDefaults.buttonColors(
            containerColor = PrimaryL3_F2FBF7,
            contentColor = PrimaryGreen_23C882,
            disabledContainerColor = Gray5_E8EAEE,
            disabledContentColor = Gray4_C5CAD2

        )
    else
        ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen_23C882,
            contentColor = White_FFFFFF,
            disabledContainerColor = Gray5_E8EAEE,
            disabledContentColor = Gray4_C5CAD2
        )

    Button(
        onClick = { onClick() },
        colors = buttonColors,
        enabled = enabled,
        shape = if (isRoundedCorner) RoundedCornerShape(8.dp) else ButtonDefaults.shape,
        modifier = if (size != null) Modifier.size(size.width.dp, size.height.dp) else Modifier,
        content = {
            if (icon != null) icon()
            Text(text = label, style = MaterialTheme.typography.b5)
        }
    )
}


@Preview
@Composable
fun PreviewFilledButton() {
    Column {
        // Filled Button
        FilledButton({}, "text")
        FilledButton({}, "text", enabled = false)
        FilledButton({}, "text", icon = { Icon(Icons.Filled.Add, "Add") }, isTonal = true)

        // Rounded Corner Shape Button
        FilledButton({}, "label", isRoundedCorner = true)
        FilledButton({}, "label", isRoundedCorner = true, enabled = false)
        FilledButton({}, "label", isRoundedCorner = true, size = Size(150, 36))
    }
}
