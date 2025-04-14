package daily.dayo.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.PrimaryL1_8FD9B9
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF

@Composable
fun FilledButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isTonal: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    val buttonColors = if (isTonal)
        ButtonDefaults.buttonColors(
            containerColor = PrimaryL3_F2FBF7,
            contentColor = Primary_23C882,
            disabledContainerColor = Gray5_E8EAEE,
            disabledContentColor = Gray4_C5CAD2

        )
    else
        ButtonDefaults.buttonColors(
            containerColor = Primary_23C882,
            contentColor = White_FFFFFF,
            disabledContainerColor = Gray5_E8EAEE,
            disabledContentColor = Gray4_C5CAD2
        )

    Button(
        onClick = { onClick() },
        modifier = modifier,
        enabled = enabled,
        colors = buttonColors,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        content = {
            if (icon != null) icon()
            Text(text = label, style = DayoTheme.typography.b6)
        }
    )
}

@Composable
fun FilledRoundedCornerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier? = null,
    contentModifier: Modifier? = null,
    enabled: Boolean = true,
    color: ButtonColors? = null,
    textStyle: TextStyle = DayoTheme.typography.b3,
    icon: @Composable (() -> Unit)? = null,
    radius: Int = 8,
) {
    val buttonColors = color
        ?: ButtonDefaults.buttonColors(
            containerColor = Primary_23C882,
            contentColor = White_FFFFFF,
            disabledContainerColor = PrimaryL1_8FD9B9,
            disabledContentColor = White_FFFFFF
        )

    Button(
        onClick = { onClick() },
        colors = buttonColors,
        enabled = enabled,
        shape = RoundedCornerShape(radius.dp),
        modifier = modifier ?: Modifier.fillMaxWidth(),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) icon()
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = textStyle,
                    modifier = contentModifier ?: Modifier.fillMaxWidth()
                )
            }
        },
        contentPadding = if (icon != null) PaddingValues(horizontal = 20.dp) else ButtonDefaults.ContentPadding
    )
}

@Composable
fun DayoOutlinedButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    OutlinedButton(
        onClick = { onClick() },
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White_FFFFFF,
            contentColor = Gray2_767B83
        ),
        border = BorderStroke(1.dp, Gray5_E8EAEE),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        content = {
            if (icon != null) icon()
            Text(text = label, style = DayoTheme.typography.b6)
        }
    )
}

@Composable
fun DayoTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    underline: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    textStyle: TextStyle = DayoTheme.typography.b6.copy(color = Primary_23C882)
) {
    Text(
        text = text,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick() }
        ),
        textDecoration = if (underline) TextDecoration.Underline else null,
        textAlign = textAlign,
        style = textStyle
    )
}

@Composable
fun NoRippleIconButton(
    onClick: () -> Unit,
    iconPainter: Painter,
    iconContentDescription: String,
    iconButtonModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconTintColor: Color = Color.Unspecified,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    additionalComponent: @Composable (() -> Unit)? = null
) {
    CompositionLocalProvider(
        LocalRippleTheme provides object : RippleTheme {
            @Composable
            override fun defaultColor(): Color {
                return RippleTheme.defaultRippleColor(
                    contentColor = LocalContentColor.current,
                    lightTheme = !isSystemInDarkTheme()
                )
            }

            @Composable
            override fun rippleAlpha(): RippleAlpha {
                val defaultAlpha = RippleTheme.defaultRippleAlpha(
                    contentColor = LocalContentColor.current,
                    lightTheme = !isSystemInDarkTheme()
                )
                return RippleAlpha(
                    pressedAlpha = defaultAlpha.pressedAlpha,
                    focusedAlpha = 0f,
                    draggedAlpha = defaultAlpha.draggedAlpha,
                    hoveredAlpha = defaultAlpha.hoveredAlpha
                )
            }
        }
    ) {
        IconButton(
            onClick = onClick,
            modifier = iconButtonModifier
                .defaultMinSize(1.dp, 1.dp)
                .indication(interactionSource = interactionSource, indication = null)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = iconModifier,
                    painter = iconPainter,
                    contentDescription = iconContentDescription,
                    tint = iconTintColor
                )
                additionalComponent?.invoke()
            }
        }
    }
}

@Preview
@Composable
private fun PreviewFilledButton() {
    Column {
        // Filled Button
        FilledButton(onClick = {}, label = "text")
        FilledButton(onClick = {}, label = "text", enabled = false)
        FilledButton(onClick = {}, label = "text", icon = { Icon(Icons.Filled.Add, "Add") }, isTonal = true)
    }
}

@Preview
@Composable
private fun PreviewFilledRoundedButton() {
    Column {
        // Rounded Corner Shape Button
        FilledRoundedCornerButton(onClick = {}, label = "label")
        FilledRoundedCornerButton(onClick = {}, label = "label", enabled = false)
        FilledRoundedCornerButton(
            onClick = {},
            label = "Email",
            modifier = Modifier.size(300.dp, 44.dp),
            color = ButtonDefaults.buttonColors(containerColor = Dark, contentColor = White_FFFFFF),
            icon = { Icon(Icons.Filled.Email, "Email") },
            textStyle = DayoTheme.typography.b5
        )
    }
}

@Preview
@Composable
private fun PreviewDayoOutlinedButton() {
    Column {
        // Outlined Button
        DayoOutlinedButton(onClick = {}, label = "text")
        DayoOutlinedButton(onClick = {}, label = "text", icon = { Icon(Icons.Filled.Add, "Add") })
    }
}

@Preview(showBackground = true, backgroundColor = android.graphics.Color.WHITE.toLong())
@Composable
private fun PreviewDayoTextButton() {
    Column {
        // Text Button
        DayoTextButton(onClick = {}, text = "text")
        DayoTextButton(onClick = {}, text = "text", underline = true)
        DayoTextButton(onClick = {}, text = "text", textStyle = DayoTheme.typography.b6.copy(Gray2_767B83))
        DayoTextButton(onClick = {}, text = "text", textStyle = DayoTheme.typography.b6.copy(Gray4_C5CAD2))

        // Underline Text Button
        Row {
            Text(text = "DAYO의 ", style = DayoTheme.typography.caption3.copy(Gray4_C5CAD2))
            DayoTextButton(onClick = {}, text = "이용약관", textStyle = DayoTheme.typography.caption3.copy(Gray4_C5CAD2), underline = true)
            Text(text = "입니다.", style = DayoTheme.typography.caption3.copy(Gray4_C5CAD2))
        }
    }
}