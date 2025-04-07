package daily.dayo.presentation.screen.settings

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.screen.account.SetPasswordView
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.Loading
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val PASSWORD_PERMIT_FORMAT = "^[a-z0-9]{8,16}$"

@Composable
@Preview
fun ChangePasswordScreen(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var changePasswordStep by remember { mutableStateOf(ChangePasswordStep.CUR_PASSWORD_INPUT) }
    val setChangePasswordStep: (ChangePasswordStep) -> Unit = { changePasswordStep = it }
    val changePasswordStatus by remember { mutableStateOf(Status.LOADING) } // TODO: ViewModel 연결

    val isNextButtonEnabled = remember { mutableStateOf(false) }
    val isNextButtonClickable = remember { mutableStateOf(false) }

    // Current Password
    val currentPasswordState = remember { mutableStateOf("") }
    val isCurrentPasswordPassFormatError = remember { mutableStateOf(false) }

    // Current Password
    val newPasswordState = remember { mutableStateOf("") }
    val isNewPasswordPassFormatError = remember { mutableStateOf(false) }
    val newPasswordConfirmState = remember { mutableStateOf("") }
    val isNewPasswordMatchError = remember { mutableStateOf(false) }


    LaunchedEffect(changePasswordStatus) {
        if (changePasswordStep == ChangePasswordStep.NEW_PASSWORD_CONFIRM &&
            changePasswordStatus == Status.SUCCESS
        ) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.change_password_success_message))
            }
        }
    }

    ChangePasswordScaffold(
        context = context,
        onBackClick = {
            when (changePasswordStep) {
                ChangePasswordStep.CUR_PASSWORD_INPUT -> {
                    onBackClick()
                }

                ChangePasswordStep.NEW_PASSWORD_INPUT -> {
                    currentPasswordState.value = ""
                    isCurrentPasswordPassFormatError.value = false

                    newPasswordState.value = ""
                    isNewPasswordPassFormatError.value = false
                    setChangePasswordStep(ChangePasswordStep.CUR_PASSWORD_INPUT)
                }

                ChangePasswordStep.NEW_PASSWORD_CONFIRM -> {
                    newPasswordConfirmState.value = ""
                    isNewPasswordMatchError.value = false
                    setChangePasswordStep(ChangePasswordStep.NEW_PASSWORD_INPUT)
                }
            }
        },
        changePasswordStep = changePasswordStep,
        title = when (changePasswordStep) {
            ChangePasswordStep.CUR_PASSWORD_INPUT -> stringResource(
                R.string.setting_change_password_current_title
            )

            ChangePasswordStep.NEW_PASSWORD_INPUT, ChangePasswordStep.NEW_PASSWORD_CONFIRM -> stringResource(
                R.string.setting_change_password_new_title
            )
        },
        isNextButtonEnabled = isNextButtonEnabled.value,
        isNextButtonClickable = isNextButtonClickable.value,
        onNextClick = {
            keyboardController?.hide()
            isNextButtonClickable.value = false

            when (changePasswordStep) {
                ChangePasswordStep.CUR_PASSWORD_INPUT -> {
                    if (
                        true // TODO: 비밀번호 확인 API 호출
                    ) {
                        setChangePasswordStep(ChangePasswordStep.NEW_PASSWORD_INPUT)
                    } else {
                        isCurrentPasswordPassFormatError.value = true
                    }
                }

                ChangePasswordStep.NEW_PASSWORD_INPUT -> {
                    val passwordFormat = Regex(PASSWORD_PERMIT_FORMAT)
                    if (passwordFormat.matches(newPasswordState.value)) {
                        setChangePasswordStep(ChangePasswordStep.NEW_PASSWORD_CONFIRM)
                    } else {
                        isNewPasswordPassFormatError.value = true
                    }
                }

                ChangePasswordStep.NEW_PASSWORD_CONFIRM -> {
                    if (newPasswordState.value == newPasswordConfirmState.value) {
                        // TODO 비밀번호 변경 API 호출
                    } else {
                        isNewPasswordMatchError.value = true
                    }
                }
            }
        }
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
        )
        when (changePasswordStep) {
            ChangePasswordStep.CUR_PASSWORD_INPUT -> {
                SetPasswordView(
                    passwordInputViewCondition = changePasswordStep == ChangePasswordStep.CUR_PASSWORD_INPUT,
                    isNextButtonEnabled = isNextButtonEnabled.value,
                    setNextButtonEnabled = { isNextButtonEnabled.value = it },
                    isNextButtonClickable = isNextButtonClickable.value,
                    setIsNextButtonClickable = { isNextButtonClickable.value = it },
                    password = currentPasswordState.value,
                    setPassword = { currentPasswordState.value = it },
                    isPasswordFormatValid = !isCurrentPasswordPassFormatError.value,
                    setIsPasswordFormatValid = { isCurrentPasswordPassFormatError.value = !it },
                )
            }

            ChangePasswordStep.NEW_PASSWORD_INPUT, ChangePasswordStep.NEW_PASSWORD_CONFIRM -> {
                SetPasswordView(
                    passwordInputViewCondition = changePasswordStep == ChangePasswordStep.NEW_PASSWORD_INPUT,
                    passwordConfirmationViewCondition = changePasswordStep == ChangePasswordStep.NEW_PASSWORD_CONFIRM,
                    isNextButtonEnabled = isNextButtonEnabled.value,
                    setNextButtonEnabled = { isNextButtonEnabled.value = it },
                    isNextButtonClickable = isNextButtonClickable.value,
                    setIsNextButtonClickable = { isNextButtonClickable.value = it },
                    password = newPasswordState.value,
                    setPassword = { newPasswordState.value = it },
                    isPasswordFormatValid = !isNewPasswordPassFormatError.value,
                    setIsPasswordFormatValid = { isNewPasswordPassFormatError.value = !it },
                    passwordConfirmation = newPasswordConfirmState.value,
                    setPasswordConfirmation = { newPasswordConfirmState.value = it },
                    isPasswordMismatch = isNewPasswordMatchError.value,
                    setIsPasswordMismatch = { isNewPasswordMatchError.value = it }
                )

            }
        }
    }

    Loading(
        isVisible = false, // TODO: Status로 조건 설정
    )
}

@Preview
@Composable
fun ChangePasswordScaffold(
    context: Context = LocalContext.current,
    onBackClick: () -> Unit = {},
    changePasswordStep: ChangePasswordStep = ChangePasswordStep.CUR_PASSWORD_INPUT,
    title: String = "",
    isNextButtonEnabled: Boolean = false,
    isNextButtonClickable: Boolean = false,
    onNextClick: () -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit) = {},
) {
    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            ChangePasswordActionbarLayout(
                onBackClick = onBackClick,
                changePasswordStep = changePasswordStep,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .background(DayoTheme.colorScheme.background)
                    .padding(horizontal = 18.dp, vertical = 0.dp)
                    .fillMaxWidth()
                    .wrapContentSize()
            ) {
                ChangePasswordTitleLayout(title = title)
                content()
            }

            Spacer(modifier = Modifier.weight(1f))
            ChangePasswordBottomLayout(
                onNextClick = { onNextClick() },
                isChangePasswordButtonEnabled = isNextButtonEnabled,
                isChangePasswordButtonClickable = isNextButtonClickable,
            )
        }
    }
}


@Preview
@Composable
fun ChangePasswordActionbarLayout(
    onBackClick: () -> Unit = {},
    changePasswordStep: ChangePasswordStep = ChangePasswordStep.CUR_PASSWORD_INPUT,
) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
    )
}

@Preview
@Composable
fun ChangePasswordTitleLayout(
    title: String = "",
) {
    Spacer(modifier = Modifier.height(15.dp))
    Text(
        text = title,
        style = DayoTheme.typography.h2.copy(color = Dark),
    )
}

@Preview
@Composable
fun ChangePasswordBottomLayout(
    onNextClick: () -> Unit = {},
    isChangePasswordButtonEnabled: Boolean = false,
    isChangePasswordButtonClickable: Boolean = false,
) {
    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        ChangePasswordNextButton(
            onButtonClick = { onNextClick() },
            isChangePasswordButtonEnabled = isChangePasswordButtonEnabled,
            isChangePasswordButtonClickable = isChangePasswordButtonClickable
        )
    }
}

@Composable
@Preview
fun ChangePasswordNextButton(
    onButtonClick: () -> Unit = {},
    isChangePasswordButtonEnabled: Boolean = false,
    isChangePasswordButtonClickable: Boolean = false,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledRoundedCornerButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            label = stringResource(R.string.next),
            textStyle = DayoTheme.typography.b3.copy(color = White_FFFFFF),
            onClick = { if (isChangePasswordButtonClickable) onButtonClick() },
            enabled = isChangePasswordButtonEnabled,
        )
    }
}

enum class ChangePasswordStep(val stepNum: Int) {
    CUR_PASSWORD_INPUT(1),      // 현재 비밀번호 입력
    NEW_PASSWORD_INPUT(2),      // 비밀번호 입력
    NEW_PASSWORD_CONFIRM(3),    // 비밀번호 재입력
}