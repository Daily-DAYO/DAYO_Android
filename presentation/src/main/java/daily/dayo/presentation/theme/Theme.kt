package daily.dayo.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

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

object DayoTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val typography: DayoTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}
