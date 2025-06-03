package daily.dayo.presentation.screen.rules

import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign

@Composable
internal fun RuleRoute(
    onBackClick: () -> Unit = {},
    ruleType: RuleType
) {
    RuleScreen(onBackClick = onBackClick, ruleType = ruleType)
}

@Preview
@Composable
fun RuleScreen(
    onBackClick: () -> Unit = {},
    ruleType: RuleType = RuleType.PRIVACY_POLICY
) {
    Surface(
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .fillMaxSize()
    ) {

        Scaffold(
            topBar = {
                RuleActionbarLayout(
                    onBackClick = onBackClick,
                    ruleType = ruleType
                )
            }
        ) { paddingValues ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = false
                        overScrollMode = View.OVER_SCROLL_NEVER
                        loadUrl("${BuildConfig.BASE_URL}/${ruleType.fileName}.html")

                        setOnKeyListener(
                            View.OnKeyListener { v, keyCode, event ->
                                if (event.action != KeyEvent.ACTION_DOWN) return@OnKeyListener true
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    onBackClick()
                                    return@OnKeyListener true
                                }
                                false
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun RuleActionbarLayout(
    onBackClick: () -> Unit = {},
    ruleType: RuleType
) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        title = ruleType.koreanName,
        titleAlignment = TopNavigationAlign.CENTER
    )
}