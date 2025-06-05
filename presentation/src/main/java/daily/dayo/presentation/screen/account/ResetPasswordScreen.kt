package daily.dayo.presentation.screen.account

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.screen.account.model.EmailCertificationState
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoPasswordTextField
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.DayoTextField
import daily.dayo.presentation.view.DayoTimerTextField
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.Loading
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.ConfirmDialog
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.AccountViewModel.Companion.RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_CODE_FAIL
import daily.dayo.presentation.viewmodel.AccountViewModel.Companion.EMAIL_CERTIFICATE_AUTH_CODE_INITIAL
import kotlinx.coroutines.CoroutineScope

const val RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_TIME_OUT = 180

@Composable
internal fun ResetPasswordRoute(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit = {},
    navigateToSignIn: () -> Unit = {},
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val resetPasswordStep = remember { mutableStateOf(ResetPasswordStep.EMAIL_INPUT) }
    val isNextButtonEnabled = remember { mutableStateOf(false) }
    val isNextButtonClickable = remember { mutableStateOf(false) }

    val email = remember { mutableStateOf("") }
    val emailCertification =
        remember { mutableStateOf(EmailCertificationState.BEFORE_CERTIFICATION) }
    val isEmailExist by accountViewModel.checkEmailSuccess.collectAsStateWithLifecycle()
    val isOAuthEmail by accountViewModel.checkOAuthEmailSuccess.collectAsStateWithLifecycle()
    val certificateEmailAuthCode by accountViewModel.certificateEmailAuthCode.collectAsStateWithLifecycle()
    val certificationInputCode = remember { mutableStateOf("") }
    val isEmailCertificateError: MutableState<Boolean?> = remember { mutableStateOf(null) }

    val password = remember { mutableStateOf("") }
    val isPasswordFormatValid = remember { mutableStateOf(true) }
    val passwordConfirmation = remember { mutableStateOf("") }
    val isPasswordMismatch = remember { mutableStateOf(false) }

    val resetPasswordStatus by accountViewModel.resetPasswordSuccess.collectAsStateWithLifecycle()

    var isCheckingEmail by remember { mutableStateOf(false) }


    LaunchedEffect(resetPasswordStatus) {
        if (resetPasswordStep.value == ResetPasswordStep.NEW_PASSWORD_CONFIRM &&
            resetPasswordStatus == Status.SUCCESS
        ) {
            onBackClick()
        }
    }

    LaunchedEffect(isEmailExist) {
        if (!isCheckingEmail) return@LaunchedEffect
        when (isEmailExist) {
            Status.SUCCESS -> {
                accountViewModel.requestCheckOAuthEmail(email.value)
            }

            Status.ERROR -> {
                emailCertification.value = EmailCertificationState.NOT_EXIST_EMAIL
                isCheckingEmail = false
            }

            Status.LOADING -> {
                emailCertification.value = EmailCertificationState.IN_PROGRESS_CHECK_EMAIL
            }
        }
    }

    LaunchedEffect(isOAuthEmail) {
        if (!isCheckingEmail || isEmailExist != Status.SUCCESS) return@LaunchedEffect

        when (isOAuthEmail) {
            Status.SUCCESS -> {
                emailCertification.value = EmailCertificationState.SUCCESS_CHECK_EMAIL
                accountViewModel.requestCertificateEmailPasswordReset(email.value)
                resetPasswordStep.value = ResetPasswordStep.EMAIL_VERIFICATION
                isCheckingEmail = false
            }

            Status.ERROR -> {
                emailCertification.value = EmailCertificationState.OAUTH_EMAIL
                isCheckingEmail = false
            }

            Status.LOADING -> {
                emailCertification.value = EmailCertificationState.IN_PROGRESS_CHECK_EMAIL
            }
        }
    }

    ResetPasswordScreen(
        context = context,
        coroutineScope = coroutineScope,
        snackBarHostState = snackBarHostState,
        onBackClick = onBackClick,
        resetPasswordStep = resetPasswordStep.value,
        setResetPasswordStep = { resetPasswordStep.value = it },
        hideKeyboard = { keyboardController?.hide() },
        isNextButtonEnabled = isNextButtonEnabled.value,
        setNextButtonEnabled = { isNextButtonEnabled.value = it },
        isNextButtonClickable = isNextButtonClickable.value,
        setNextButtonClickable = { isNextButtonClickable.value = it },
        email = email.value,
        setEmail = { email.value = it },
        emailCertification = emailCertification.value,
        setEmailCertification = { emailCertification.value = it },
        requestIsEmailExist = { accountViewModel.requestCheckEmail(it) },
        isEmailExist = isEmailExist,
        requestIsOAuthEmail = { accountViewModel.requestCheckOAuthEmail(it) },
        isOAuthEmail = isOAuthEmail,
        certificateEmailAuthCode = certificateEmailAuthCode
            ?: EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString(),
        certificationInputCode = certificationInputCode.value,
        setCertificationInputCode = { certificationInputCode.value = it },
        isEmailCertificateError = isEmailCertificateError.value,
        setIsEmailCertificateError = { isEmailCertificateError.value = it },
        requestEmailCertification = { accountViewModel.requestCertificateEmailPasswordReset(it) },
        password = password.value,
        setPassword = { password.value = it },
        isPasswordFormatValid = isPasswordFormatValid.value,
        setIsPasswordFormatValid = { isPasswordFormatValid.value = it },
        passwordConfirmation = passwordConfirmation.value,
        setPasswordConfirmation = { passwordConfirmation.value = it },
        isPasswordMismatch = isPasswordMismatch.value,
        setIsPasswordMismatch = { isPasswordMismatch.value = it },
        requestResetPassword = {
            accountViewModel.requestChangePassword(
                email.value,
                password.value
            )
        },
        isCheckingEmail = isCheckingEmail,
        setIsCheckingEmail = { isCheckingEmail = it }
    )

    if (emailCertification.value == EmailCertificationState.OAUTH_EMAIL) {
        ConfirmDialog(
            title = stringResource(R.string.reset_password_email_certification_fail_oauth_account_dialog_title),
            description = stringResource(R.string.reset_password_email_certification_fail_oauth_account_dialog_description),
            onClickConfirmText = stringResource(R.string.confirm),
            onClickConfirm = { navigateToSignIn() },
            onClickCancel = null
        )
    }

    Loading(
        isVisible = resetPasswordStatus == Status.LOADING || resetPasswordStatus == Status.SUCCESS,
        lottieFile = R.raw.dayo_loading,
        lottieModifier = Modifier
            .width(82.dp)
            .height(85.dp),
        message = stringResource(R.string.reset_password_alert_message_loading)
    )
}

@Preview
@Composable
fun ResetPasswordScreen(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit = {},
    resetPasswordStep: ResetPasswordStep = ResetPasswordStep.EMAIL_INPUT,
    setResetPasswordStep: (ResetPasswordStep) -> Unit = {},
    hideKeyboard: () -> Unit = {},
    isNextButtonEnabled: Boolean = false,
    setNextButtonEnabled: (Boolean) -> Unit = {},
    isNextButtonClickable: Boolean = false,
    setNextButtonClickable: (Boolean) -> Unit = {},
    email: String = "",
    setEmail: (String) -> Unit = {},
    isEmailExist: Status = Status.LOADING,
    requestIsEmailExist: (String) -> Unit = {},
    isOAuthEmail: Status = Status.LOADING,
    requestIsOAuthEmail: (String) -> Unit = {},
    emailCertification: EmailCertificationState = EmailCertificationState.BEFORE_CERTIFICATION,
    setEmailCertification: (EmailCertificationState) -> Unit = {},
    certificateEmailAuthCode: String = EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString(),
    requestEmailCertification: (String) -> Unit = {},
    certificationInputCode: String = "",
    setCertificationInputCode: (String) -> Unit = {},
    isEmailCertificateError: Boolean? = null,
    setIsEmailCertificateError: (Boolean) -> Unit = {},
    password: String = "",
    setPassword: (String) -> Unit = {},
    isPasswordFormatValid: Boolean = false,
    setIsPasswordFormatValid: (Boolean) -> Unit = {},
    passwordConfirmation: String = "",
    setPasswordConfirmation: (String) -> Unit = {},
    isPasswordMismatch: Boolean = false,
    setIsPasswordMismatch: (Boolean) -> Unit = {},
    requestResetPassword: () -> Unit = {},
    isCheckingEmail: Boolean = false,
    setIsCheckingEmail: (Boolean) -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .background(DayoTheme.colorScheme.background)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .background(DayoTheme.colorScheme.background)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            ResetPasswordActionbarLayout(
                onBackClick = onBackClick,
                resetPasswordStep = resetPasswordStep,
                backStep = {
                    when (resetPasswordStep) {
                        ResetPasswordStep.EMAIL_INPUT -> {
                            setEmail("")
                            setEmailCertification(EmailCertificationState.BEFORE_CERTIFICATION)
                            setResetPasswordStep(ResetPasswordStep.EMAIL_INPUT)
                        }

                        ResetPasswordStep.EMAIL_VERIFICATION -> {
                            setEmail("")
                            setEmailCertification(EmailCertificationState.BEFORE_CERTIFICATION)
                            setEmailCertification(EmailCertificationState.BEFORE_CERTIFICATION)
                            setCertificationInputCode("")
                            setIsEmailCertificateError(false)
                            setResetPasswordStep(ResetPasswordStep.EMAIL_INPUT)
                        }

                        ResetPasswordStep.NEW_PASSWORD_INPUT -> {
                            setEmail("")
                            setEmailCertification(EmailCertificationState.BEFORE_CERTIFICATION)
                            setEmailCertification(EmailCertificationState.BEFORE_CERTIFICATION)
                            setCertificationInputCode("")
                            setIsEmailCertificateError(false)
                            setPassword("")
                            setIsPasswordFormatValid(true)
                            setResetPasswordStep(ResetPasswordStep.EMAIL_INPUT)
                        }

                        ResetPasswordStep.NEW_PASSWORD_CONFIRM -> {
                            setPassword("")
                            setIsPasswordFormatValid(true)
                            setPasswordConfirmation("")
                            setIsPasswordMismatch(false)
                            setResetPasswordStep(ResetPasswordStep.NEW_PASSWORD_INPUT)
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .background(DayoTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 0.dp)
                    .fillMaxWidth()
                    .wrapContentSize()
            ) {
                // Title 영역
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (resetPasswordStep) {
                        ResetPasswordStep.EMAIL_INPUT, ResetPasswordStep.EMAIL_VERIFICATION ->
                            stringResource(R.string.reset_password_email_title)

                        ResetPasswordStep.NEW_PASSWORD_INPUT, ResetPasswordStep.NEW_PASSWORD_CONFIRM ->
                            stringResource(R.string.reset_password_new_password_title)
                    },
                    style = DayoTheme.typography.h1.copy(color = Dark),
                )

                // SubTitle 영역
                AnimatedVisibility(
                    visible = (resetPasswordStep.stepNum in 1..2),
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                    Text(
                        text = if (resetPasswordStep == ResetPasswordStep.EMAIL_INPUT)
                            stringResource(R.string.reset_password_email_sub_title)
                        else if (resetPasswordStep == ResetPasswordStep.EMAIL_VERIFICATION)
                            stringResource(R.string.reset_password_new_password_sub_title)
                        else "",
                        style = DayoTheme.typography.b6.copy(color = Gray2_767B83),
                    )
                }

                // Contents 영역
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                )
                when (resetPasswordStep) {
                    ResetPasswordStep.EMAIL_INPUT -> {
                        EmailInputLayout(
                            context = context,
                            isNextButtonEnabled = isNextButtonEnabled,
                            setNextButtonEnabled = setNextButtonEnabled,
                            isNextButtonClickable = isNextButtonClickable,
                            setIsNextButtonClickable = setNextButtonClickable,
                            email = email,
                            setEmail = setEmail,
                            emailCertification = emailCertification,
                            setEmailCertification = setEmailCertification,
                            isEmailExist = isEmailExist,
                            requestIsEmailExist = requestIsEmailExist,
                            isOAuthEmail = isOAuthEmail,
                            requestIsOAuthEmail = requestIsOAuthEmail,
                            requestEmailCertification = requestEmailCertification,
                        )
                    }

                    ResetPasswordStep.EMAIL_VERIFICATION -> {
                        EmailCertificationLayout(
                            resetPasswordStep = resetPasswordStep,
                            isNextButtonEnabled = isNextButtonEnabled,
                            setNextButtonEnabled = setNextButtonEnabled,
                            isNextButtonClickable = isNextButtonClickable,
                            setIsNextButtonClickable = setNextButtonClickable,
                            email = email,
                            emailCertification = emailCertification,
                            certificationCode = certificateEmailAuthCode,
                            certificationInputCode = certificationInputCode,
                            setCertificationInputCode = setCertificationInputCode,
                            isEmailCertificateError = isEmailCertificateError,
                            setIsEmailCertificateError = setIsEmailCertificateError,
                            requestEmailCertification = requestEmailCertification,
                        )
                    }

                    ResetPasswordStep.NEW_PASSWORD_INPUT, ResetPasswordStep.NEW_PASSWORD_CONFIRM -> {
                        NewPasswordLayout(
                            resetPasswordStep = resetPasswordStep,
                            isNextButtonEnabled = isNextButtonEnabled,
                            setNextButtonEnabled = setNextButtonEnabled,
                            isNextButtonClickable = isNextButtonClickable,
                            setIsNextButtonClickable = setNextButtonClickable,
                            password = password,
                            setPassword = setPassword,
                            isPasswordFormatValid = isPasswordFormatValid,
                            setIsPasswordFormatValid = setIsPasswordFormatValid,
                            passwordConfirmation = passwordConfirmation,
                            setPasswordConfirmation = setPasswordConfirmation,
                            isPasswordMismatch = isPasswordMismatch,
                            setIsPasswordMismatch = setIsPasswordMismatch,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            ResetPasswordBottomLayout(
                resetPasswordStep = resetPasswordStep,
                onNextClick = {
                    hideKeyboard()
                    setNextButtonEnabled(false)

                    when (resetPasswordStep) {
                        ResetPasswordStep.EMAIL_INPUT -> {
                            setIsCheckingEmail(true)
                            requestIsEmailExist(email)
                        }

                        ResetPasswordStep.EMAIL_VERIFICATION -> {
                            if (certificateEmailAuthCode == EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString() ||
                                certificateEmailAuthCode == RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString()
                            ) {
                                // 인증번호가 없는 상태
                            } else {
                                if (certificationInputCode == certificateEmailAuthCode) {
                                    setResetPasswordStep(ResetPasswordStep.NEW_PASSWORD_INPUT)
                                } else {
                                    setIsEmailCertificateError(true)
                                }
                            }
                        }

                        ResetPasswordStep.NEW_PASSWORD_INPUT -> {
                            val passwordFormat = Regex("^[a-z0-9]{8,16}$")
                            if (passwordFormat.matches(password)) {
                                setResetPasswordStep(ResetPasswordStep.NEW_PASSWORD_CONFIRM)
                            } else {
                                setIsPasswordFormatValid(false)
                            }
                        }

                        ResetPasswordStep.NEW_PASSWORD_CONFIRM -> {
                            if (password == passwordConfirmation) {
                                requestResetPassword()
                            } else {
                                setIsPasswordMismatch(true)
                            }
                        }
                    }
                },
                isNextButtonEnabled = isNextButtonEnabled,
                isNextButtonClickable = isNextButtonClickable,
            )
        }
    }
}

@Preview
@Composable
fun ResetPasswordActionbarLayout(
    onBackClick: () -> Unit = {},
    resetPasswordStep: ResetPasswordStep = ResetPasswordStep.EMAIL_INPUT,
    backStep: () -> Unit = {}
) {
    BackHandler {
        if (resetPasswordStep == ResetPasswordStep.EMAIL_INPUT) {
            onBackClick()
        } else {
            backStep()
        }
    }

    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = {
                    if (resetPasswordStep == ResetPasswordStep.EMAIL_INPUT) {
                        onBackClick()
                    } else {
                        backStep()
                    }
                },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
    )
}

@Preview
@Composable
fun ResetPasswordBottomLayout(
    resetPasswordStep: ResetPasswordStep = ResetPasswordStep.EMAIL_INPUT,
    onNextClick: () -> Unit = {},
    isNextButtonEnabled: Boolean = false,
    isNextButtonClickable: Boolean = false,
) {
    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        ResetPasswordNextButton(
            resetPasswordStep = resetPasswordStep,
            onClick = onNextClick,
            isEnabled = isNextButtonEnabled,
            isClickable = isNextButtonClickable,
        )
    }
}

@Preview
@Composable
fun ResetPasswordNextButton(
    resetPasswordStep: ResetPasswordStep = ResetPasswordStep.EMAIL_INPUT,
    onClick: () -> Unit = {},
    isEnabled: Boolean = false,
    isClickable: Boolean = false,
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
            label = when (resetPasswordStep) {
                ResetPasswordStep.EMAIL_INPUT -> stringResource(R.string.reset_password_email_next_button)
                ResetPasswordStep.EMAIL_VERIFICATION -> stringResource(R.string.reset_password_email_certification_next_button)
                ResetPasswordStep.NEW_PASSWORD_INPUT -> stringResource(R.string.reset_password_new_password_next_button)
                ResetPasswordStep.NEW_PASSWORD_CONFIRM -> stringResource(R.string.reset_password_new_password_confirmation_next_button)
                else -> ""
            },
            textStyle = DayoTheme.typography.b3.copy(color = White_FFFFFF),
            onClick = { if (isClickable) onClick() },
            enabled = isEnabled,
        )
    }
}


@Preview
@Composable
fun EmailInputLayout(
    context: Context = LocalContext.current,
    isNextButtonEnabled: Boolean = false,
    setNextButtonEnabled: (Boolean) -> Unit = {},
    isNextButtonClickable: Boolean = false,
    setIsNextButtonClickable: (Boolean) -> Unit = {},
    email: String = "",
    setEmail: (String) -> Unit = {},
    emailCertification: EmailCertificationState = EmailCertificationState.BEFORE_CERTIFICATION,
    setEmailCertification: (EmailCertificationState) -> Unit = {},
    isEmailExist: Status = Status.LOADING,
    requestIsEmailExist: (String) -> Unit = {},
    isOAuthEmail: Status = Status.LOADING,
    requestIsOAuthEmail: (String) -> Unit = {},
    requestEmailCertification: (String) -> Unit = {},
) {
    val lastErrorMessage = remember { mutableStateOf("") }

    LaunchedEffect(emailCertification) {
        lastErrorMessage.value = when (emailCertification) {
            EmailCertificationState.BEFORE_CERTIFICATION -> ""
            EmailCertificationState.IN_PROGRESS_CHECK_EMAIL -> lastErrorMessage.value
            EmailCertificationState.INVALID_FORMAT -> context.getString(R.string.reset_password_email_fail_invalid_format)
            EmailCertificationState.NOT_EXIST_EMAIL -> context.getString(R.string.reset_password_email_fail_not_exist)
            EmailCertificationState.SUCCESS_CHECK_EMAIL -> ""
            EmailCertificationState.OAUTH_EMAIL -> "소셜 로그인 계정입니다"
            else -> lastErrorMessage.value
        }
    }

    DayoTextField(
        value = email,
        onValueChange = {
            setEmail(it)
            val formatValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
            setNextButtonEnabled(formatValid)
            setIsNextButtonClickable(formatValid)
            if (!formatValid) {
                setEmailCertification(EmailCertificationState.INVALID_FORMAT)
            } else {
                lastErrorMessage.value = ""
                // INVALID FORMAT 에러 메시지가 다음 에러 메시지가 표시될 떄 남아 있지 않도록 value Clear
            }
        },
        label = stringResource(R.string.email),
        placeholder = stringResource(R.string.reset_password_email_placeholder),
        trailingIconId = if (email.isNotBlank()) R.drawable.ic_trailing_check else null,
        errorTrailingIconId = R.drawable.ic_trailing_error,
        errorMessage = lastErrorMessage.value,
        isError = if (email.isBlank()) null else !isNextButtonEnabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    )
}

@Preview
@Composable
private fun EmailCertificationLayout(
    resetPasswordStep: ResetPasswordStep = ResetPasswordStep.EMAIL_VERIFICATION,
    isNextButtonEnabled: Boolean = false,
    setNextButtonEnabled: (Boolean) -> Unit = {},
    isNextButtonClickable: Boolean = false,
    setIsNextButtonClickable: (Boolean) -> Unit = {},
    email: String = "",
    emailCertification: EmailCertificationState = EmailCertificationState.BEFORE_CERTIFICATION,
    certificationCode: String = "-1",
    certificationInputCode: String = "",
    setCertificationInputCode: (String) -> Unit = {},
    isEmailCertificateError: Boolean? = null,
    setIsEmailCertificateError: (Boolean) -> Unit = {},
    requestEmailCertification: (String) -> Unit = {},
) {
    val certificateEmailAuthCodeFormat = Regex("^\\d{6}$")

    var tryCount by remember { mutableStateOf(1) }
    val isPaused = remember { mutableStateOf(false) }
    val seconds = remember {
        mutableStateOf(RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_TIME_OUT)
    }
    val timerErrorMessageRedId =
        remember { mutableStateOf((R.string.reset_password_email_certification_fail_wrong)) }

    setNextButtonEnabled(
        certificateEmailAuthCodeFormat.matches(certificationInputCode)
    )
    setIsNextButtonClickable(
        certificateEmailAuthCodeFormat.matches(certificationInputCode)
    )

    key(tryCount) {
        DayoTimerTextField(
            value = certificationInputCode,
            onValueChange = { textValue ->
                setCertificationInputCode(textValue)
            },
            seconds = seconds.value,
            isPaused = isPaused.value,
            label = if (certificationInputCode.isBlank()) " "
            else stringResource(R.string.reset_password_email_certification_input_title),
            placeholder = if (certificationInputCode.isBlank())
                stringResource(R.string.reset_password_email_certification_input_placeholder) else "",
            isError = isEmailCertificateError ?: false,
            errorMessage = stringResource(timerErrorMessageRedId.value),
            timeOutErrorMessage = stringResource(R.string.reset_password_email_certification_fail_time_out),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
    )
    if (certificationCode != EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString()) {
        // 네트워크 오류로 정상적으로 비교할 인증코드를 보내지 못한 경우 에러 처리
        if (certificationCode == RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString()) {
            timerErrorMessageRedId.value =
                R.string.reset_password_email_certification_fail_network
            setIsEmailCertificateError(true)
        }
    }

    AnimatedVisibility(
        visible = certificationCode != RESET_PASSWORD_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString() &&
                certificationCode != EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString(),
        enter = fadeIn(),
        exit = shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DayoTextButton(
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd),
                text = stringResource(R.string.reset_password_email_certification_resend_button),
                onClick = {
                    tryCount++
                    requestEmailCertification(email)
                },
                underline = true,
                textStyle = DayoTheme.typography.b6.copy(Gray2_767B83),
            )
        }

    }
}


@Composable
@Preview
private fun NewPasswordLayout(
    resetPasswordStep: ResetPasswordStep = ResetPasswordStep.NEW_PASSWORD_INPUT,
    isNextButtonEnabled: Boolean = false,
    setNextButtonEnabled: (Boolean) -> Unit = {},
    isNextButtonClickable: Boolean = false,
    setIsNextButtonClickable: (Boolean) -> Unit = {},
    password: String = "",
    setPassword: (String) -> Unit = {},
    isPasswordFormatValid: Boolean = false,
    setIsPasswordFormatValid: (Boolean) -> Unit = {},
    passwordConfirmation: String = "",
    setPasswordConfirmation: (String) -> Unit = {},
    isPasswordMismatch: Boolean = false,
    setIsPasswordMismatch: (Boolean) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        setNextButtonEnabled(
            if (resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_INPUT) password.isNotBlank()
            else passwordConfirmation.isNotBlank()
        )
        setIsNextButtonClickable(
            if (resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_INPUT) password.isNotBlank()
            else passwordConfirmation.isNotBlank()
        )

        AnimatedVisibility(
            visible = resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_CONFIRM,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            DayoPasswordTextField(
                value = passwordConfirmation,
                onValueChange = {
                    setIsPasswordMismatch(false) // 입력 시, 에러메시지 제거
                    setPasswordConfirmation(it)
                },
                label = if (passwordConfirmation.isBlank()) " "
                else stringResource(R.string.reset_password_new_password_confirm_input_title),
                placeholder = if (passwordConfirmation.isBlank()) stringResource(R.string.reset_password_new_password_confirm_input_placeholder) else "",
                isError = isPasswordMismatch,
                errorMessage = stringResource(R.string.reset_password_new_password_confirm_fail_not_match),
            )
        }

        if (resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_CONFIRM) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
        }

        AnimatedVisibility(
            visible = resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_INPUT || resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_CONFIRM,
            enter = slideInVertically(),
        ) {
            DayoPasswordTextField(
                value = password,
                onValueChange = {
                    setIsPasswordFormatValid(true) // 입력 시, 에러메시지 제거
                    setPassword(it)
                },
                label = if (password.isBlank()) " "
                else stringResource(R.string.reset_password_new_password_input_title),
                placeholder = if (password.isBlank())
                    stringResource(R.string.reset_password_new_password_input_placeholder) else "",
                isError = if (resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_INPUT) !isPasswordFormatValid else false,
                errorMessage = stringResource(R.string.reset_password_new_password_message_format_fail),
                isEnabled = resetPasswordStep == ResetPasswordStep.NEW_PASSWORD_INPUT,
            )
        }
    }
}

enum class ResetPasswordStep(val stepNum: Int) {
    EMAIL_INPUT(1),             // 이메일 주소 입력
    EMAIL_VERIFICATION(2),      // 인증번호 입력
    NEW_PASSWORD_INPUT(3),      // 비밀번호 입력
    NEW_PASSWORD_CONFIRM(4),    // 비밀번호 재입력
}