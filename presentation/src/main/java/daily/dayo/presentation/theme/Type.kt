package daily.dayo.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import daily.dayo.presentation.R

val PretendardKrFontFamily = FontFamily(
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

val Typography.h1: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 33.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.h2: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 33.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.h3: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 27.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.b1: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 27.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.b2: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 27.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.b3: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.b4: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.b5: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.b6: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.caption1: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 19.5.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.caption2: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.caption3: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing= (-0.4).sp
        )
}
val Typography.caption4: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 15.sp,
            letterSpacing= (-0.4).sp
        )
}

val Typography.caption5: TextStyle
    @Composable
    get() {
        return TextStyle(
            fontFamily = PretendardKrFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 15.sp,
            letterSpacing= (-0.4).sp
        )
}