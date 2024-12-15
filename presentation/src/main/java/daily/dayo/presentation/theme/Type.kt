package daily.dayo.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import daily.dayo.presentation.R

private val PretendardKrFontFamily = FontFamily(
    Font(R.font.pretendard_black, FontWeight.Black),
    Font(R.font.pretendard_extra_bold, FontWeight.ExtraBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_extra_light, FontWeight.ExtraLight),
    Font(R.font.pretendard_thin, FontWeight.Thin)
)

internal val Typography = DayoTypography()

@Immutable
data class DayoTypography(
    // Heading
    val h1: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 33.sp
    ),

    val h2: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 33.sp
    ),

    val h3: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 30.sp
    ),

    val h4: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 30.sp
    ),

    val h5: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 27.sp
    ),

    // Body
    val b1: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 27.sp
    ),

    val b2: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 27.sp
    ),

    val b3: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 24.sp
    ),

    val b4: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 24.sp
    ),

    val b5: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 20.sp
    ),

    val b6: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 20.sp
    ),

    // Caption
    val caption1: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 19.5.sp
    ),

    val caption2: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 19.5.sp
    ),

    val caption3: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 18.sp
    ),

    val caption4: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 18.sp
    ),

    val caption5: TextStyle = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 15.sp
    ),

    val caption6: TextStyle = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = PretendardKrFontFamily,
        letterSpacing = (-0.4).sp,
        lineHeight = 15.sp
    )
)

internal val LocalTypography = staticCompositionLocalOf { DayoTypography() }
