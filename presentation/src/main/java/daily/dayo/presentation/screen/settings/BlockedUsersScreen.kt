package daily.dayo.presentation.screen.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.size.Size
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.view.DayoOutlinedButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BlockedUsersScreen(
    coroutineScope: CoroutineScope,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    Scaffold(
        topBar = { BlockedUsersActionbarLayout(onBackClick = onBackClick) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .background(DayoTheme.colorScheme.background)
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // TODO: Replace with actual data
                    items(10) {
                        BlockedUser(
                            imageUrl = "",
                            nickName = "Blocked User Name $it",
                            onUnblockClick = { userId ->
                                coroutineScope.launch {
                                    profileViewModel.requestUnblockMember(userId)
                                }
                            },
                            context = context,
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun BlockedUser(
    imageUrl: String = "",
    nickName: String = "",
    onUnblockClick: (String) -> Unit = {},
    context: Context = LocalContext.current,
) {
    Row(
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .fillMaxWidth()
            .height(38.dp)
            .padding(bottom = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundImageView(
            imageUrl = imageUrl,
            context = context,
            modifier = Modifier
                .size(36.dp)
                .aspectRatio(1f),
            imageDescription = "User Profile Image",
            imageSize = Size(36, 36),
            roundSize = 18.dp,
            placeholderResId = R.drawable.ic_profile_default_user_profile,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = nickName,
            style = DayoTheme.typography.b6.copy(color = Dark),
            maxLines = 1,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        DayoOutlinedButton(
            label = stringResource(R.string.blocked_users_unblock),
            onClick = {
                onUnblockClick("userId") // Replace with actual user ID
            },
            modifier = Modifier.height(36.dp),
        )
    }
}


@Preview
@Composable
fun BlockedUsersActionbarLayout(
    onBackClick: () -> Unit = {},
) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        title = stringResource(R.string.blocked_users_title),
    )
}