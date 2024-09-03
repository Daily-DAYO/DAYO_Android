package daily.dayo.presentation.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption5
import daily.dayo.presentation.theme.h1
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.ProfileViewModel

@Composable
fun MyPageScreen(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileInfo = profileViewModel.profileInfo.observeAsState()

    LaunchedEffect(Unit) {
        profileViewModel.requestMyProfile()
    }

    Scaffold(
        topBar = { MyPageTopNavigation() },
        content = { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                MyPageProfile(profileInfo.value?.data)
            }
        }
    )
}

@Composable
private fun MyPageProfile(profile: Profile?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White_FFFFFF)
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // profile image
        RoundImageView(
            context = LocalContext.current,
            imageUrl = "${BuildConfig.BASE_URL}/images/${profile?.profileImg}",
            imageDescription = "my page profile image",
            roundSize = 24.dp,
            customModifier = Modifier
                .size(48.dp)
                .clickableSingle(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { }
                )
        )

        // nickname, email
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = profile?.nickname ?: "",
                style = MaterialTheme.typography.h1.copy(
                    color = Gray1_313131,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = profile?.email ?: "",
                style = MaterialTheme.typography.caption5.copy(
                    color = Gray4_C5CAD2
                )
            )
        }

        // follower
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_follower),
                contentDescription = "follower",
                modifier = Modifier.clickableSingle { /*TODO*/ }
            )

            Text(
                text = "${profile?.followerCount ?: ""}".padStart(3, '0'),
                style = MaterialTheme.typography.b6.copy(color = Color(0xFF50545B))
            )
        }

        // following
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_following),
                contentDescription = "following",
                modifier = Modifier.clickableSingle { /*TODO*/ }
            )

            Text(
                text = "${profile?.followingCount ?: ""}".padStart(3, '0'),
                style = MaterialTheme.typography.b6.copy(color = Color(0xFF50545B))
            )
        }
    }
}

@Composable
private fun MyPageTopNavigation() {
    TopNavigation(
        leftIcon = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(start = 18.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.my_page),
                    style = MaterialTheme.typography.h1.copy(
                        color = Gray1_313131,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        rightIcon = {
            NoRippleIconButton(
                onClick = { },
                iconContentDescription = "setting button",
                iconPainter = painterResource(id = R.drawable.ic_setting),
                iconModifier = Modifier
                    .padding(end = 18.dp)
                    .size(24.dp),
                iconTintColor = Gray1_313131
            )
        }
    )
}

@Preview
@Composable
private fun PreviewMyPageTopNavigation() {
    MyPageTopNavigation()
}


@Preview
@Composable
private fun PreviewMyPageProfile() {
    MyPageProfile(profile = null)
}