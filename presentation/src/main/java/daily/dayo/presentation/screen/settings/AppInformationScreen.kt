package daily.dayo.presentation.screen.settings

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.rules.RuleType
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign

@Composable
fun AppInformationScreen(
    onBackClick: () -> Unit = {},
    onRulesClick: (RuleType) -> Unit = {},
) {
    Scaffold(
        topBar = { AppInformationTopBar(onBackClick = onBackClick) },
    ) { paddingValues ->
        AppInformationContent(
            modifier = Modifier.padding(paddingValues),
            onRulesClick = onRulesClick,
        )
    }
}

@Composable
fun AppInformationTopBar(
    onBackClick: () -> Unit,
) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        title = stringResource(R.string.information_title),
        titleAlignment = TopNavigationAlign.CENTER
    )
}

@Composable
fun AppInformationContent(
    modifier: Modifier = Modifier,
    onRulesClick: (RuleType) -> Unit = {},
) {
    val context = LocalContext.current
    val appVersion = getAppVersion(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 20.dp),
    ) {
        AppInformationItem(
            title = stringResource(R.string.rules_terms_and_conditions),
            onClick = { onRulesClick(RuleType.TERMS_AND_CONDITIONS) },
        )
        Spacer(modifier = Modifier.height(2.dp))
        AppInformationItem(
            title = stringResource(R.string.rules_privacy_policy_title),
            onClick = { onRulesClick(RuleType.PRIVACY_POLICY) },
        )
        Spacer(modifier = Modifier.height(2.dp))
        AppInformationItem(
            title = stringResource(R.string.app_version),
            description = "DAYO $appVersion",
        )
    }
}

@Composable
fun AppInformationItem(
    title: String,
    description: String? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = DayoTheme.typography.b4,
            color = Dark,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = DayoTheme.typography.b4,
                color = DayoTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .wrapContentSize(),
            )
        } else {
            Icon(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(20.dp),
                painter = painterResource(id = R.drawable.ic_chevron_r),
                contentDescription = null,
                tint = DayoTheme.colorScheme.outline,
            )
        }
    }
}

private fun getAppVersion(context: Context): String {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            ).versionName
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppInformationContent() {
    DayoTheme {
        AppInformationContent()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsListItem() {
    DayoTheme {
        Column {
            AppInformationItem(title = "이용약관")
            AppInformationItem(title = "개인정보처리방침")
            AppInformationItem(title = "앱 버전", description = "DAYO 1.0.0")
        }
    }
}