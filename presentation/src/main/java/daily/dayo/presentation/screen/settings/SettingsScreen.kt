package daily.dayo.presentation.screen.settings

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.ProfileViewModel

@Composable
fun SettingsScreen(
    onProfileEditClick: () -> Unit,
    onBackClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onBlockUsersClick: () -> Unit,
    onSettingNotificationClick: () -> Unit,
    onNoticesClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileInfo = profileViewModel.profileInfo.observeAsState()

    LaunchedEffect(Unit) {
        profileViewModel.requestMyProfile()
    }

    SettingsScreen(
        profile = profileInfo.value?.data,
        onProfileEditClick = onProfileEditClick,
        onBackClick = onBackClick,
        onSettingNotificationClick = onSettingNotificationClick,
        onPasswordChangeClick = onPasswordChangeClick,
        onNoticesClick = onNoticesClick,
        onBlockUsersClick = onBlockUsersClick
    )
}

@Composable
private fun SettingsScreen(
    profile: Profile?,
    onProfileEditClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingNotificationClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onNoticesClick: () -> Unit = {},
    onBlockUsersClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.indication(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_sign),
                            contentDescription = stringResource(id = R.string.back_sign),
                            tint = Gray1_50545B
                        )
                    }
                },
                title = stringResource(id = R.string.setting_title),
                titleAlignment = TopNavigationAlign.CENTER
            )
        },
        containerColor = White
    ) { contentPadding ->
        val scrollState = rememberScrollState()
        val context = LocalContext.current
        val appVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } ?: ""

        val settingMenus = listOf(
            SettingItem(R.string.setting_menu_change_password, R.drawable.ic_setting_password_change, onClickMenu = onPasswordChangeClick),
            SettingItem(R.string.setting_menu_block_user, R.drawable.ic_block, onClickMenu = onBlockUsersClick),
            SettingItem(R.string.setting_menu_notification, R.drawable.ic_notification, onClickMenu = onSettingNotificationClick),
            null, // Divider
            SettingItem(R.string.setting_menu_notice, R.drawable.ic_setting_notice, onClickMenu = onNoticesClick),
            SettingItem(R.string.setting_menu_information, R.drawable.ic_setting_information, onClickMenu = {}, description = appVersion),
            SettingItem(R.string.setting_menu_contact, R.drawable.ic_setting_contact, onClickMenu = {}),
            null // Divider
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(contentPadding)
                .padding(top = 16.dp)
                .padding(horizontal = 20.dp)
        ) {
            SettingProfile(profile, onProfileEditClick)
            Spacer(modifier = Modifier.height(28.dp))
            settingMenus.forEach { menu ->
                menu?.run {
                    SettingMenu(
                        titleId = titleId,
                        iconId = iconId,
                        onClickMenu = onClickMenu,
                        description = description
                    )
                } ?: Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Gray6_F0F1F3
                )
            }
            Text(
                text = stringResource(id = R.string.sign_out),
                modifier = Modifier.padding(vertical = 11.5.dp, horizontal = 8.dp),
                color = Primary_23C882,
                style = DayoTheme.typography.b6
            )
            Text(
                text = stringResource(id = R.string.delete_account),
                modifier = Modifier.padding(vertical = 11.5.dp, horizontal = 8.dp),
                color = Gray3_9FA5AE,
                style = DayoTheme.typography.b6
            )
        }
    }
}

@Composable
private fun SettingProfile(
    profile: Profile?,
    onProfileEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // profile image
        RoundImageView(
            context = LocalContext.current,
            imageUrl = "${BuildConfig.BASE_URL}/images/${profile?.profileImg}",
            imageDescription = stringResource(id = R.string.setting_my_profile_image_description),
            roundSize = 28.dp,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // nickname
        Text(
            text = profile?.nickname ?: "",
            color = Dark,
            style = DayoTheme.typography.b2
        )

        // email
        Text(
            text = profile?.email ?: "",
            color = Gray3_9FA5AE,
            style = DayoTheme.typography.b6
        )

        Spacer(modifier = Modifier.height(16.dp))

        // edit profile
        TextButton(
            onClick = onProfileEditClick,
            shape = RoundedCornerShape(size = 12.dp),
            border = BorderStroke(width = 1.dp, color = Gray6_F0F1F3),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = White_FFFFFF,
                contentColor = Gray2_767B83
            ),
            modifier = Modifier.height(36.dp),
            content = {
                Text(
                    text = stringResource(id = R.string.my_profile_edit_title),
                    modifier = Modifier.padding(horizontal = 28.dp),
                    style = DayoTheme.typography.b6.copy(Gray1_50545B)
                )
            }
        )
    }
}

@Composable
private fun SettingMenu(
    @StringRes titleId: Int,
    @DrawableRes iconId: Int,
    onClickMenu: () -> Unit,
    description: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable { onClickMenu() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = stringResource(id = titleId),
            modifier = Modifier.padding(horizontal = 8.dp),
            tint = Dark
        )

        Text(
            text = stringResource(id = titleId),
            modifier = Modifier.weight(1f),
            color = Dark,
            style = DayoTheme.typography.b6
        )

        Text(
            text = description,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Gray2_767B83,
            style = DayoTheme.typography.b6
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_next),
            contentDescription = stringResource(id = titleId),
            tint = Gray4_C5CAD2
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
    DayoTheme {
        SettingsScreen(
            profile = null,
            onBackClick = {},
            onProfileEditClick = {},
            onPasswordChangeClick = {},
            onSettingNotificationClick = {},
            onNoticesClick = {},
            onBlockUsersClick = {}
        )
    }
}

data class SettingItem(
    val titleId: Int,
    val iconId: Int,
    val onClickMenu: () -> Unit,
    val description: String = ""
)
