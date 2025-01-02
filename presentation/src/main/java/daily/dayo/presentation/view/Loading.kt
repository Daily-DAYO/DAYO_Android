package daily.dayo.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray7_F6F6F7

const val LOADING_TOP_Z_INDEX = Float.MAX_VALUE

@Composable
fun Loading(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    message: String = "잠시만 기다려 주세요",
    dimColor: Color = Dark.copy(alpha = 0.4f),
    lottieFile: Int,
    lottieModifier: Modifier = Modifier,
    animationSpeed: Float = 1f,
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(dimColor)
                .zIndex(LOADING_TOP_Z_INDEX) // 다른 컴포넌트 위에 표시
                .clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    // DO NOTHING FOR BLOCKING CLICK
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieFile))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    speed = animationSpeed
                )
                LottieAnimation(
                    modifier = lottieModifier,
                    composition = composition,
                    progress = { progress },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = DayoTheme.typography.b3.copy(color = Gray7_F6F6F7),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}