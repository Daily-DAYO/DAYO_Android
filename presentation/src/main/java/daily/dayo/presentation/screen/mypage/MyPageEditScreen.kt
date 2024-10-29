package daily.dayo.presentation.screen.mypage

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.view.FilledTextField
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

    LaunchedEffect(Unit) {
        profileViewModel.requestMyProfile()
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
    val nickname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }

    LaunchedEffect(profileInfo) {
        nickname.value = profileInfo?.nickname ?: ""
        email.value = profileInfo?.email ?: ""
    }

    Scaffold(
        topBar = { MyPageEditTopNavigation(onBackClick = onBackClick, onConfirmClick = onConfirmClick) },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .background(White_FFFFFF)
                    .fillMaxSize()
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
                    placeholder = placeholder,
                    customModifier = Modifier
                        .size(118.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickableSingle(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { }
                        )
                )

                Spacer(modifier = Modifier.height(36.dp))

                // todo textfield 수정
                FilledTextField(
                    value = nickname.value,
                    onValueChange = { textValue -> nickname.value = textValue },
                    label = stringResource(id = R.string.nickname),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // todo 편집 불가능하도록 설정
                FilledTextField(
                    value = email.value,
                    onValueChange = { textValue -> email.value = textValue },
                    label = stringResource(id = R.string.email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
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