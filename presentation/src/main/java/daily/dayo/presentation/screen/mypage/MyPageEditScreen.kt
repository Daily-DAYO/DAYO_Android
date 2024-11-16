package daily.dayo.presentation.screen.mypage

import android.content.Context
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.b4
import daily.dayo.presentation.theme.caption3
import daily.dayo.presentation.view.BadgeRoundImageView
import daily.dayo.presentation.view.DayoTextField
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.view.dialog.BottomSheetDialog
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState
import daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
internal fun MyPageEditScreen(
    onBackClick: () -> Unit,
    profileSettingViewModel: ProfileSettingViewModel = hiltViewModel()
) {
    val profileUiState by profileSettingViewModel.profileInfo.observeAsState(Resource.loading(null))
    val isNicknameDuplicate by profileSettingViewModel.isNicknameDuplicate.collectAsStateWithLifecycle(false)

    val profileInfo = remember { mutableStateOf<Profile?>(null) }
    val context = LocalContext.current
    val nickNameErrorMessage = remember { mutableStateOf("") }

    LaunchedEffect(profileUiState) {
        profileInfo.value = when (profileUiState.status) {
            Status.SUCCESS -> profileUiState.data
            Status.LOADING, Status.ERROR -> null
        }
    }

    LaunchedEffect(profileInfo.value?.nickname, isNicknameDuplicate) {
        profileInfo.value?.nickname?.let { nickname ->
            profileSettingViewModel.requestCheckNicknameDuplicate(nickname)
        }

        nickNameErrorMessage.value = when {
            isNicknameDuplicate -> context.getString(R.string.my_profile_edit_nickname_message_duplicate_fail)
            else -> verifyNickname(profileInfo.value?.nickname ?: "", context)
        }
    }

    MyPageEditScreen(
        profileInfo = profileInfo,
        nickNameErrorMessage = nickNameErrorMessage.value,
        onBackClick = onBackClick,
        onConfirmClick = {}
    )
}

private fun verifyNickname(nickname: String, context: Context): String {
    return if (nickname.length < 2) context.getString(R.string.my_profile_edit_nickname_message_length_fail_min)
    else if (nickname.length > 10) context.getString(R.string.my_profile_edit_nickname_message_length_fail_max)
    else if (Pattern.matches("^[ㄱ-ㅎ|ㅏ-ㅣ가-힣a-zA-Z0-9]+$", nickname).not()) context.getString(R.string.my_profile_edit_nickname_message_format_fail)
    else ""
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProfileImageBottomSheetDialog(bottomSheetState: ModalBottomSheetState) {
    BottomSheetDialog(
        sheetState = bottomSheetState,
        buttons = listOf(
            Pair(stringResource(id = R.string.my_profile_edit_image_select_gallery)) {

            }, Pair(stringResource(id = R.string.image_option_camera)) {

            }, Pair(stringResource(id = R.string.my_profile_edit_image_reset)) {

            }),
        isFirstButtonColored = true
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyPageEditScreen(
    profileInfo: MutableState<Profile?>,
    nickNameErrorMessage: String,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = getBottomSheetDialogState()

    Scaffold(
        topBar = {
            MyPageEditTopNavigation(
                confirmEnabled = nickNameErrorMessage.isEmpty(),
                onBackClick = onBackClick,
                onConfirmClick = onConfirmClick
            )
        },
        bottomBar = { ProfileImageBottomSheetDialog(bottomSheetState) },
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
                BadgeRoundImageView(
                    context = LocalContext.current,
                    imageUrl = "${BuildConfig.BASE_URL}/images/${profileInfo.value?.profileImg}",
                    imageDescription = "my page profile image",
                    placeholder = placeholder,
                    contentModifier = Modifier
                        .size(100.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickableSingle(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                coroutineScope.launch { bottomSheetState.show() }
                            }
                        )
                )

                Spacer(modifier = Modifier.height(36.dp))

                DayoTextField(
                    value = profileInfo.value?.nickname ?: "",
                    onValueChange = { textValue ->
                        profileInfo.value = profileInfo.value?.copy(
                            nickname = textValue
                        )
                    },
                    label = stringResource(id = R.string.nickname),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    isError = nickNameErrorMessage.isNotEmpty(),
                    errorMessage = nickNameErrorMessage
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = stringResource(id = R.string.email),
                        style = MaterialTheme.typography.caption3.copy(
                            color = Gray4_C5CAD2,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = profileInfo.value?.email ?: "",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.b4.copy(
                            color = Gray2_767B83,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Gray6_F0F1F3
                    )
                }
            }
        }
    )
}

@Composable
private fun MyPageEditTopNavigation(
    confirmEnabled: Boolean,
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
                        enabled = confirmEnabled,
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
            profileInfo = remember {
                mutableStateOf(
                    Profile(
                        null,
                        "princedj@gmail.com",
                        "동준왕자다요",
                        "",
                        0,
                        0,
                        0,
                        false
                    )
                )
            },
            nickNameErrorMessage = "",
            onBackClick = {},
            onConfirmClick = {}
        )
    }
}