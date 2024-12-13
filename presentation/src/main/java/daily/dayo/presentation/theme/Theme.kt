package daily.dayo.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColorScheme = lightColorScheme(
    primary = Primary_23C882,
    background = White_FFFFFF
)

@Composable
fun DayoTheme(content: @Composable () -> Unit) {

    CompositionLocalProvider(
        LocalTypography provides Typography
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            content = content
        )
    }
}

val LocalTypography = staticCompositionLocalOf { DayoTypography() }

object DayoTheme {
    val typography: DayoTypography
        @Composable
        get() = LocalTypography.current
}
