package daily.dayo.presentation.screen.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
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
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.ProfileViewModel

@Composable
fun SettingsScreen(
    onProfileEditClick: () -> Unit,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileInfo = profileViewModel.profileInfo.observeAsState()

    LaunchedEffect(Unit) {
        profileViewModel.requestMyProfile()
    }

    SettingsScreen(
        profile = profileInfo.value?.data,
        onProfileEditClick = onProfileEditClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun SettingsScreen(
    profile: Profile?,
    onProfileEditClick: () -> Unit,
    onBackClick: () -> Unit
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
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(top = 16.dp)
                .verticalScroll(scrollState)
        ) {
            SettingProfile(profile, onProfileEditClick)
            Spacer(modifier = Modifier.height(28.dp))
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
                    style = DayoTheme.typography.b6.copy(Gray1_50545B),
                )
            }
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
    DayoTheme {
        SettingsScreen(
            profile = null,
            onProfileEditClick = {},
            onBackClick = {}
        )
    }
}


