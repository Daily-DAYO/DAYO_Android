package daily.dayo.presentation.screen.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
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
import daily.dayo.presentation.view.dialog.ConfirmDialog
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.SettingNotificationViewModel

enum class NotificationSettingType {
    DEVICE,
    NOTICE,
    REACTION,
}

data class NotificationSettingField(
    val type: NotificationSettingType = NotificationSettingType.DEVICE,
    val title: String,
    val description: String = "",
    val isChecked: Boolean = false,
)

@Composable
@Preview
fun SettingNotificationScreen(
    onBackClick: () -> Unit = {},
    accountViewModel: AccountViewModel = hiltViewModel(),
    settingNotificationViewModel: SettingNotificationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val reactionNotificationChannel = notificationManager.getNotificationChannel("REACTION")

    var isNotificationEnabledOnDevice by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }
    val isNoticeNotificationEnabledOnDevice by accountViewModel.isNoticeNotificationEnabled.collectAsStateWithLifecycle()
    var isReactionNotificationEnabledOnDevice by remember {
        // 채널이 없거나, 중요도가 NONE이면 꺼진 것
        mutableStateOf(reactionNotificationChannel != null && reactionNotificationChannel.importance != NotificationManager.IMPORTANCE_NONE)
    }
    val isReactionNotificationEnabledOnServer by settingNotificationViewModel.isReactionNotificationEnabled.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingNotificationViewModel.requestReceiveAlarm()
    }

    LaunchedEffect(isNoticeNotificationEnabledOnDevice) {
        with(settingNotificationViewModel) {
            if (isNoticeNotificationEnabledOnDevice) registerDeviceToken() else unregisterFcmToken()
        }
    }

    DisposableEffect(lifecycleOwner) {
        // 시스템에서 알림 설정 변경 후 돌아왔을 때, 반영되도록 RESUME에서 체크
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isNotificationEnabledOnDevice =
                    NotificationManagerCompat.from(context).areNotificationsEnabled()
                val updatedChannel =
                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                        .getNotificationChannel("REACTION")

                isReactionNotificationEnabledOnDevice =
                    updatedChannel != null && updatedChannel.importance != NotificationManager.IMPORTANCE_NONE
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SettingNotificationTopNavigation(onBackClick = onBackClick) }
    ) { innerPadding ->
        SettingNotificationContent(
            innerPadding = innerPadding,
            isNotificationEnabledOnDevice = isNotificationEnabledOnDevice,
            isNoticeNotificationOnDevice = isNoticeNotificationEnabledOnDevice,
            isReactionNotificationEnabledOnDevice = isReactionNotificationEnabledOnDevice,
            isReactionNotificationEnabledOnServer = isReactionNotificationEnabledOnServer,
            changeNoticeNotificationSetting = { isChecked ->
                accountViewModel.changeNoticeNotificationSetting(isChecked)

                // Subscribe/Unsubscribe Topic (Notice Notification)
                val topic = context.getString(R.string.notification_topic_notice)
                Firebase.messaging.run {
                    (if (isChecked) subscribeToTopic(topic) else unsubscribeFromTopic(topic))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, if (isChecked) "NOTICE 수신" else "NOTICE 수신 거부")
                            }
                        }
                }
            },
            changeReactionNotificationSetting = {
                settingNotificationViewModel.requestReceiveChangeReceiveAlarm(it)
            },
            reactionNotificationChannel = reactionNotificationChannel
        )
    }
}

@Composable
@Preview
fun MoveToDeviceSettingDialog(
    onConfirmClick: () -> Unit = {},
    onDismissClick: () -> Unit = {}
) {
    ConfirmDialog(
        title = stringResource(R.string.move_to_setting_notification_dialog_title),
        description = "",
        onClickConfirmText = stringResource(R.string.move),
        onClickConfirm = onConfirmClick,
        onClickCancelText = stringResource(R.string.cancel),
        onClickCancel = onDismissClick,
    )
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
    context: Context = LocalContext.current,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    isNotificationEnabledOnDevice: Boolean = false,
    isNoticeNotificationOnDevice: Boolean = false,
    isReactionNotificationEnabledOnDevice: Boolean? = false,
    isReactionNotificationEnabledOnServer: Boolean? = false,
    changeNoticeNotificationSetting: (Boolean) -> Unit = {},
    changeReactionNotificationSetting: (Boolean) -> Unit = {},
    reactionNotificationChannel: NotificationChannel? = null,
) {
    val showMoveToDeviceSettingDialog = remember { mutableStateOf(false) }
    val moveDestination = remember { mutableStateOf(SettingMoveDestination.App) }

    val notificationSettingsList = listOf(
        NotificationSettingField(
            type = NotificationSettingType.DEVICE,
            title = stringResource(R.string.setting_notification_device),
            description = stringResource(R.string.setting_notification_device_explanation),
            isChecked = isNotificationEnabledOnDevice,
        ),
        NotificationSettingField(
            type = NotificationSettingType.NOTICE,
            title = stringResource(R.string.setting_notification_notice),
            isChecked = isNoticeNotificationOnDevice,
        ),
        NotificationSettingField(
            type = NotificationSettingType.REACTION,
            title = stringResource(R.string.setting_notification_reaction),
            isChecked = (
                    (isReactionNotificationEnabledOnDevice ?: false || reactionNotificationChannel == null) // channel이 null이면 채널 생성이 아직 안된것 -> 알림을 받아야 채널이 생성되므로 true 처리
                            && isReactionNotificationEnabledOnServer ?: false)
        )
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
            onSwitchChange = { settingField, isChecked ->
                when (settingField.type) {
                    NotificationSettingType.DEVICE -> {
                        moveDestination.value = SettingMoveDestination.App
                        showMoveToDeviceSettingDialog.value = true
                    }

                    NotificationSettingType.NOTICE -> {
                        changeNoticeNotificationSetting(isChecked)
                    }

                    NotificationSettingType.REACTION -> {
                        val updatedChannel =
                            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                                .getNotificationChannel("REACTION")

                        if (updatedChannel != null &&
                            updatedChannel.importance == NotificationManager.IMPORTANCE_NONE
                        ) {
                            // 채널이 존재하는데, 꺼져있으면 직접 다시 켜야 함
                            moveDestination.value = SettingMoveDestination.Reaction
                            showMoveToDeviceSettingDialog.value = true
                        } else {
                            changeReactionNotificationSetting(isChecked)
                        }
                    }
                }
            },
            isNotificationChangeEnabled = isNotificationEnabledOnDevice
        )

        if (showMoveToDeviceSettingDialog.value) {
            MoveToDeviceSettingDialog(
                onConfirmClick = {
                    val intent = when (moveDestination.value) {
                        SettingMoveDestination.App -> {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        }

                        SettingMoveDestination.Reaction -> {
                            Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                putExtra(
                                    Settings.EXTRA_CHANNEL_ID,
                                    context.getString(R.string.notification_channel_id)
                                )
                            }
                        }
                    }
                    context.startActivity(intent)
                    showMoveToDeviceSettingDialog.value = false
                },
                onDismissClick = { showMoveToDeviceSettingDialog.value = false }
            )
        }
    }
}

@Composable
fun NotificationSettingsList(
    settings: List<NotificationSettingField> = emptyList(),
    onSwitchChange: (NotificationSettingField, Boolean) -> Unit = { _, _ -> },
    isNotificationChangeEnabled: Boolean = true, // Device 알림이 꺼져있으면 설정 변경 불가처리
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
                onSwitchChange = { onSwitchChange(setting, it) },
                isNotificationChangeEnabled =
                (setting.type == NotificationSettingType.DEVICE || isNotificationChangeEnabled)
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
    isChecked: Boolean = false,
    isNotificationChangeEnabled: Boolean = true,
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
            enabled = isNotificationChangeEnabled,
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

enum class SettingMoveDestination {
    App,
    Reaction,
}