package daily.dayo.presentation.screen.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme

@Preview
@Composable
fun SignUpCompleteScreen(nickname: String = "닉네임") {
    val completeLottieSpec: LottieCompositionSpec =
        LottieCompositionSpec.RawRes(R.raw.signup_email_complete_dayo_logo)
    val completeLottie by rememberLottieComposition(completeLottieSpec)
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(0.dp, 12.dp, 0.dp, 0.dp),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = DayoTheme.typography.h1.fontWeight)) {
                    append(nickname)
                }
                append("님,\n가입을 축하드려요!")
            },
            style = DayoTheme.typography.h2.copy(color = Dark),
            textAlign = TextAlign.Center,
        )

        LottieAnimation(
            composition = completeLottie,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center),
        )
    }
}