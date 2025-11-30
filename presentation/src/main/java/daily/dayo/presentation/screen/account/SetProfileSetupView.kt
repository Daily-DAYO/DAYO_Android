package daily.dayo.presentation.screen.account

import LocalBottomSheetController
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.screen.account.model.NicknameCertificationState
import daily.dayo.presentation.view.BadgeRoundImageView
import daily.dayo.presentation.view.DayoTextField

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SetProfileSetupView(
    context: Context = LocalContext.current,
    isNextButtonEnabled: MutableState<Boolean> = remember { mutableStateOf(false) },
    isNextButtonClickable: MutableState<Boolean> = remember { mutableStateOf(false) },
    nicknameState: MutableState<String> = remember { mutableStateOf("") },
    nicknameCertificationState: MutableState<NicknameCertificationState> = remember {
        mutableStateOf(
            NicknameCertificationState.BEFORE_CERTIFICATION
        )
    },
    requestIsNicknameDuplicate: (String) -> Unit = {},
    profileImg: Bitmap? = null,
) {
    val bottomSheetController = LocalBottomSheetController.current
    val placeholderResId = remember { R.drawable.ic_profile_default }
    val interactionSource = remember { MutableInteractionSource() }
    val profileImageClickModifier = remember {
        Modifier
            .size(110.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(percent = 50))
            .clickableSingle(
                interactionSource = interactionSource,
                indication = null,
                onClick = { bottomSheetController.show() }
            )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgeRoundImageView(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            context = context,
            imageUrl = profileImg ?: "",
            imageDescription = "Set Profile image",
            placeholderResId = placeholderResId,
            roundSize = 55.dp,
            contentModifier = profileImageClickModifier
        )
        Spacer(modifier = Modifier.height(36.dp))
        SetNicknameLayout(
            context = context,
            isNextButtonEnabled = isNextButtonEnabled.value,
            setNextButtonEnabled = { isNextButtonEnabled.value = it },
            setIsNextButtonClickable = { isNextButtonClickable.value = it },
            nickname = nicknameState.value,
            setNickname = { nicknameState.value = it },
            nicknameCertification = nicknameCertificationState.value,
            setNicknameCertification = { nicknameCertificationState.value = it },
            requestIsNicknameDuplicate = { requestIsNicknameDuplicate(it) },
        )
    }
}

@Preview
@Composable
fun SetNicknameLayout(
    context: Context = LocalContext.current,
    isNextButtonEnabled: Boolean = false,
    setNextButtonEnabled: (Boolean) -> Unit = {},
    setIsNextButtonClickable: (Boolean) -> Unit = {},
    nickname: String = "",
    setNickname: (String) -> Unit = {},
    nicknameCertification: NicknameCertificationState = NicknameCertificationState.BEFORE_CERTIFICATION,
    setNicknameCertification: (NicknameCertificationState) -> Unit = {},
    requestIsNicknameDuplicate: (String) -> Unit = {},
) {
    val nicknamePermitFormatRegex = Regex(NICKNAME_PERMIT_FORMAT)
    setNextButtonEnabled(nicknameCertification == NicknameCertificationState.SUCCESS)
    setIsNextButtonClickable(nicknameCertification == NicknameCertificationState.SUCCESS)

    DayoTextField(
        value = nickname,
        onValueChange = {
            setNickname(it)
            if (nicknamePermitFormatRegex.matches(it)) {
                requestIsNicknameDuplicate(it)
            }
        },
        label = if (nickname.isBlank()) " "
        else stringResource(R.string.sign_up_email_set_profile_nickname_input_title),
        placeholder = if (nickname.isBlank())
            stringResource(R.string.sign_up_email_set_profile_nickname_input_placeholder) else "",
        isError = if (nickname.isBlank()) null else !isNextButtonEnabled,
        trailingIconId = if (nickname.isNotBlank()) R.drawable.ic_trailing_delete else null,
        errorTrailingIconId = R.drawable.ic_trailing_delete,
        onTrailingIconClick = { setNickname("") },
        errorMessage = when (nicknameCertification) {
            NicknameCertificationState.DUPLICATE_NICKNAME -> stringResource(R.string.sign_up_email_set_profile_nickname_message_duplicate_fail)
            NicknameCertificationState.INVALID_FORMAT -> stringResource(R.string.sign_up_email_set_profile_nickname_message_format_fail)
            else -> ""
        },
    )
}