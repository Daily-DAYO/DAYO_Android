package daily.dayo.presentation.view

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption3

@Composable
fun FilledButton(
    onClick: () -> Unit,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isTonal: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
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
        modifier = modifier,
        content = {
            if (icon != null) icon()
            Text(text = label, style = MaterialTheme.typography.b6)
        }
    )
}

@Composable
fun FilledRoundedCornerButton(
    onClick: () -> Unit,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.b3,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    color: ButtonColors? = null
) {
    val buttonColors = color
        ?: ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen_23C882,
            contentColor = White_FFFFFF,
            disabledContainerColor = Gray5_E8EAEE,
            disabledContentColor = Gray4_C5CAD2
        )

    Button(
        onClick = { onClick() },
        colors = buttonColors,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) icon()
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        contentPadding = if (icon != null) PaddingValues(horizontal = 20.dp) else ButtonDefaults.ContentPadding
    )
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    androidx.compose.material3.OutlinedButton(
        onClick = { onClick() },
        border = BorderStroke(1.dp, Gray5_E8EAEE),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White_FFFFFF,
            contentColor = Gray2_767B83
        ),
        modifier = modifier,
        content = {
            if (icon != null) icon()
            Text(text = label, style = MaterialTheme.typography.b6)
        }
    )
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    text: String = "",
    textStyle: TextStyle = MaterialTheme.typography.b6.copy(color = PrimaryGreen_23C882),
    underline: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick() }
        ),
        text = text,
        style = textStyle,
        textDecoration = if (underline) TextDecoration.Underline else null
    )
}

@Preview
@Composable
fun PreviewFilledButton() {
    Column {
        // Filled Button
        FilledButton(onClick = {}, label = "text")
        FilledButton(onClick = {}, label = "text", enabled = false)
        FilledButton(onClick = {}, label = "text", icon = { Icon(Icons.Filled.Add, "Add") }, isTonal = true)
    }
}

@Preview
@Composable
fun PreviewFilledRoundedButton() {
    Column {
        // Rounded Corner Shape Button
        FilledRoundedCornerButton(onClick = {}, label = "label")
        FilledRoundedCornerButton(onClick = {}, label = "label", enabled = false)
        FilledRoundedCornerButton(
            onClick = {},
            label = "Email",
            modifier = Modifier.size(300.dp, 44.dp),
            color = ButtonDefaults.buttonColors(containerColor = Gray1_313131, contentColor = White_FFFFFF),
            icon = { Icon(Icons.Filled.Email, "Email") },
            textStyle = MaterialTheme.typography.b5
        )
    }
}

@Preview
@Composable
fun PreviewOutlinedButton() {
    Column {
        // Outlined Button
        OutlinedButton(onClick = {}, label = "text")
        OutlinedButton(onClick = {}, label = "text", icon = { Icon(Icons.Filled.Add, "Add") })
    }
}

@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
@Composable
fun PreviewTextButton() {
    Column {
        // Text Button
        TextButton(onClick = {}, text = "text")
        TextButton(onClick = {}, text = "text", underline = true)
        TextButton(onClick = {}, text = "text", textStyle = MaterialTheme.typography.b6.copy(Gray2_767B83))
        TextButton(onClick = {}, text = "text", textStyle = MaterialTheme.typography.b6.copy(Gray4_C5CAD2))

        // Underline Text Button
        Row {
            Text(text = "DAYO의 ", style = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2))
            TextButton(onClick = {}, text = "이용약관", textStyle = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2), underline = true)
            Text(text = "입니다.", style = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2))
        }
    }
}