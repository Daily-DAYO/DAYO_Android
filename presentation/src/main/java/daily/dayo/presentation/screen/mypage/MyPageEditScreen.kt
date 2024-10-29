package daily.dayo.presentation.screen.mypage

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.ProfileViewModel

@Composable
internal fun MyPageEditScreen(
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val profileUiState by profileViewModel.profileInfo.observeAsState(Resource.loading(null))
    val profileInfo = when (profileUiState.status) {
        Status.SUCCESS -> profileUiState.data
        Status.LOADING, Status.ERROR -> null
    }
    MyPageEditScreen(
        profileInfo = profileInfo,
        onBackClick = onBackClick,
        onConfirmClick = {}
    )
}

@Composable
private fun MyPageEditScreen(
    profileInfo: Profile?,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { MyPageEditTopNavigation(onBackClick = onBackClick, onConfirmClick = onConfirmClick) },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(contentPadding)
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val placeholder = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_profile_default_user_profile)
                RoundImageView(
                    context = LocalContext.current,
                    imageUrl = "${BuildConfig.BASE_URL}/images/${profileInfo?.profileImg}",
                    imageDescription = "my page profile image",
                    roundSize = 24.dp,
                    placeholder = placeholder,
                    customModifier = Modifier
                        .size(118.dp)
                        .clickableSingle(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { }
                        )
                )
            }
        }
    )
}

@Composable
private fun MyPageEditTopNavigation(
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    TopNavigation(
        title = stringResource(id = R.string.my_profile_edit_title),
        leftIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_sign),
                    contentDescription = "back",
                    tint = Gray1_50545B
                )
            }
        },
        rightIcon = {
            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .padding(start = 24.dp, end = 18.dp)
                    .clickableSingle(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onConfirmClick
                    ),
                text = stringResource(id = R.string.confirm),
                style = MaterialTheme.typography.b3.copy(color = Dark),
            )
        },
        titleAlignment = TopNavigationAlign.CENTER
    )
}

@Preview
@Composable
internal fun PreviewMyPageEditScreen() {
    MaterialTheme {
        MyPageEditScreen(
            Profile(
                null,
                "princedj@gmail.com",
                "동준왕자다요",
                "",
                0,
                0,
                0,
                false
            ),
            {}, {}
        )
    }
}