package daily.dayo.presentation.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign

data class NotificationSettingField(
    val title: String,
    val description: String = "",
    val isChecked: Boolean = false,
)

@Composable
@Preview
fun SettingNotificationScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SettingNotificationTopNavigation(onBackClick = onBackClick) }
    ) { innerPadding ->
        SettingNotificationContent(innerPadding)
    }
}

@Composable
@Preview
fun SettingNotificationTopNavigation(
    onBackClick: () -> Unit = {}
) {
    TopNavigation(
        title = stringResource(R.string.setting_notification_title),
        leftIcon = {
            NoRippleIconButton(
                onClick = {
                    onBackClick()
                },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        titleAlignment = TopNavigationAlign.CENTER,
    )
}

@Composable
@Preview
fun SettingNotificationContent(
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    val notificationSettingsList = listOf(
        NotificationSettingField(
            title = stringResource(R.string.setting_notification_device),
            description = stringResource(R.string.setting_notification_device_explanation)
        ),
        NotificationSettingField(title = stringResource(R.string.setting_notification_notice)),
        NotificationSettingField(title = stringResource(R.string.setting_notification_reaction))
    )

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )

        NotificationSettingsList(
            settings = notificationSettingsList,
            onSwitchChange = { _, _ -> } // TODO Implement Change action
        )
    }
}

@Composable
fun NotificationSettingsList(
    settings: List<NotificationSettingField> = emptyList(),
    onSwitchChange: (NotificationSettingField, Boolean) -> Unit = { _, _ -> },
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(settings) { setting ->
            SettingNotificationItem(
                title = setting.title,
                description = setting.description,
                isChecked = setting.isChecked,
                onSwitchChange = { onSwitchChange(setting, it) }
            )
        }
    }
}


@Composable
@Preview
fun SettingNotificationItem(
    title: String = "",
    description: String = "",
    onSwitchChange: (Boolean) -> Unit = {},
    isChecked: Boolean = false
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .wrapContentSize()
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Start,
                style = DayoTheme.typography.b4.copy(color = Dark),
                modifier = Modifier.wrapContentSize(),
            )
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    textAlign = TextAlign.Start,
                    style = DayoTheme.typography.b6.copy(color = Gray2_767B83),
                    modifier = Modifier.wrapContentSize(),
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { onSwitchChange(it) },
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = White_FFFFFF,
                checkedThumbColor = White_FFFFFF,
                checkedTrackColor = Primary_23C882,
                uncheckedTrackColor = Gray6_F0F1F3,
                uncheckedBorderColor = Gray6_F0F1F3,
                checkedBorderColor = Primary_23C882,
            )
        )
    }
}