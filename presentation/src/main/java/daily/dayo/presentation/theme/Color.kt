package daily.dayo.presentation.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Transparent = Color(0x00000000)
val Dark = Color(0xFF313131)
val Primary_23C882 = Color(0xFF23C882)
val White_FFFFFF = Color(0xFFFFFFFF)

val Gray1_50545B = Color(0xFF50545B)
val Gray2_767B83 = Color(0xFF767B83)
val Gray3_9FA5AE = Color(0xFF9FA5AE)
val Gray4_C5CAD2 = Color(0xFFC5CAD2)
val Gray5_E8EAEE = Color(0xFFE8EAEE)
val Gray6_F0F1F3 = Color(0xFFF0F1F3)
val Gray7_F6F6F7 = Color(0xFFF6F6F7)

val Pink_F34D7A = Color(0xFFF34D7A)
val Blue_597CF2 = Color(0xFF597CF2)
val Yellow_FFEB5A = Color(0xFFFFEB5A)
val Red_FF4545 = Color(0xFFFF4545)

val PrimaryL1_8FD9B9 = Color(0xFFBEEBD3)
val PrimaryL2_E6FBF1 = Color(0xFFE6FBF1)
val PrimaryL3_F2FBF7 = Color(0xFFF2FBF7)
val Transparent_White30 = Color(0x4DFFFFFF)
val primaryD1_0EB36E = Color(0xFF0EB36E)

val LightColorScheme = lightColorScheme(
    primary = Primary_23C882,
    background = White_FFFFFF
)

internal val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }
