package daily.dayo.presentation.screen.account

import LocalBottomSheetController
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.image.ImageResizeUtil.USER_PROFILE_THUMBNAIL_RESIZE_SIZE
import daily.dayo.presentation.common.image.ImageResizeUtil.resizeBitmap
import daily.dayo.presentation.common.image.launchCamera
import daily.dayo.presentation.common.image.launchGallery
import daily.dayo.presentation.screen.account.model.EmailCertificationState
import daily.dayo.presentation.screen.account.model.NicknameCertificationState
import daily.dayo.presentation.screen.account.model.SignUpStep
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.Loading
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.ProfileImageBottomSheetDialog
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.AccountViewModel.Companion.EMAIL_CERTIFICATE_AUTH_CODE_INITIAL
import daily.dayo.presentation.viewmodel.AccountViewModel.Companion.SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL
import daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val SIGN_UP_EMAIL_CERTIFICATE_AUTH_TIME_OUT = 180
const val NICKNAME_PERMIT_FORMAT = "^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]{2,10}$"
const val PASSWORD_PERMIT_FORMAT = "^[a-z0-9]{8,16}$"
const val IMAGE_TEMP_FILE_NAME_FORMAT = "yyyy-MM-d-HH-mm-ss-SSS"
const val IMAGE_TEMP_FILE_EXTENSION = ".jpg"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignUpEmailRoute(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit = {},
    accountViewModel: AccountViewModel = hiltViewModel(),
    profileSettingViewModel: ProfileSettingViewModel = hiltViewModel(),
    startSignUpStep: SignUpStep = SignUpStep.EMAIL_INPUT
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val keyboardController = LocalSoftwareKeyboardController.current
    val bitmapOptions = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }

    var signUpStep by remember { mutableStateOf(startSignUpStep) }
    val isEmailDuplicate by accountViewModel.isEmailDuplicate.collectAsStateWithLifecycle()
    val certificateEmailAuthCode by accountViewModel.certificateEmailAuthCode.collectAsStateWithLifecycle()
    val isNicknameDuplicate by accountViewModel.isNicknameDuplicate.collectAsStateWithLifecycle()
    var showProfileGallery by remember { mutableStateOf(false) }
    var showProfileCapture by remember { mutableStateOf(false) }
    val profileImgState = remember { mutableStateOf<Bitmap?>(null) }
    val signUpStatus by accountViewModel.signupSuccess.collectAsStateWithLifecycle()
    val updateProfileStatus by profileSettingViewModel.isUpdateSuccess.collectAsStateWithLifecycle()

    val openGallery = launchGallery(
        context = context,
        onImageSelected = { uri ->
            if (uri != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(uri)
                    inputStream?.use {
                        BitmapFactory.decodeStream(it, null, bitmapOptions)?.let {
                            profileImgState.value = it
                        }
                    }
                }
            }
        },
        onPermissionDenied = {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.permission_fail_message_gallery))
            }
        }
    )
    val openCamera = launchCamera(
        context = context,
        onImageCaptured = { bitmap ->
            bitmap?.let { profileImgState.value = it }
        },
        onPermissionDenied = {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.permission_fail_message_camera))
            }
        }
    )

    // bottom sheet
    val bottomSheetController = LocalBottomSheetController.current
    val bottomSheetContent: @Composable () -> Unit = remember {
        {
            ProfileImageBottomSheetDialog(
                onClickProfileSelect = {
                    showProfileGallery = true
                    bottomSheetController.hide()
                },
                onClickProfileCapture = {
                    showProfileCapture = true
                    bottomSheetController.hide()

                },
                onClickProfileReset = {
                    profileImgState.value = null
                    bottomSheetController.hide()
                }
            )
        }
    }
    DisposableEffect(Unit) {
        bottomSheetController.setContent(bottomSheetContent)
        onDispose {
            bottomSheetController.hide()
        }
    }

    if (showProfileGallery) {
        openGallery()
        showProfileGallery = false
    }

    if (showProfileCapture) {
        openCamera()
        showProfileCapture = false
    }

    SignUpEmailScreen(
        context = context,
        hideKeyboard = { keyboardController?.hide() },
        onBackClick = onBackClick,
        requestIsEmailDuplicate = { accountViewModel.requestCheckEmailDuplicate(it) },
        onCertificateEmailClick = {
            coroutineScope.launch {
                accountViewModel.requestCertificateEmail(it)
                snackBarHostState.showSnackbar(context.getString(R.string.sign_up_email_set_address_certification_send_snackbar))
            }
        },
        requestIsNicknameDuplicate = { accountViewModel.requestCheckNicknameDuplicate(it) },
        onProfileUpdateClick = { nickname, profileImg ->
            val imageFileTimeFormat = SimpleDateFormat(IMAGE_TEMP_FILE_NAME_FORMAT, Locale.KOREA)
            val fileName = imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() +
                    IMAGE_TEMP_FILE_EXTENSION

            coroutineScope.launch {
                val resizedProfileImg = withContext(Dispatchers.Default) {
                    profileImg?.let {
                        resizeBitmap(
                            it,
                            USER_PROFILE_THUMBNAIL_RESIZE_SIZE,
                            USER_PROFILE_THUMBNAIL_RESIZE_SIZE
                        )
                    }
                }

                profileSettingViewModel.requestUpdateMyProfileWithResizedFile(
                    nickname = nickname,
                    profileImg = resizedProfileImg,
                    profileImgTempDir = "${context.cacheDir}/$fileName",
                    isReset = profileImg == null
                )
            }
        },
        onSignUpEmailClick = { email, nickname, password, profileImg ->
            val imageFileTimeFormat = SimpleDateFormat(IMAGE_TEMP_FILE_NAME_FORMAT, Locale.KOREA)
            val fileName = imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() +
                    IMAGE_TEMP_FILE_EXTENSION

            coroutineScope.launch {
                val resizedProfileImg = withContext(Dispatchers.Default) {
                    profileImg?.let {
                        resizeBitmap(
                            it,
                            USER_PROFILE_THUMBNAIL_RESIZE_SIZE,
                            USER_PROFILE_THUMBNAIL_RESIZE_SIZE
                        )
                    }
                }

                accountViewModel.requestSignupEmail(
                    email = email,
                    nickname = nickname,
                    password = password,
                    profileImg = resizedProfileImg,
                    profileImgTempDir = "${context.cacheDir}/$fileName"
                )
            }
        },
        isEmailDuplicate = isEmailDuplicate,
        certificateEmailAuthCode = certificateEmailAuthCode
            ?: EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString(),
        isNicknameDuplicate = isNicknameDuplicate,
        profileImg = profileImgState.value,
        signUpStatus = signUpStatus,
        updateProfileStatus = updateProfileStatus,
        startSignUpStep = startSignUpStep,
        signUpStep = signUpStep,
        setSignUpStep = { signUpStep = it }
    )
    Loading(
        isVisible = (signUpStatus == Status.LOADING || updateProfileStatus == Status.LOADING),
        message = stringResource(R.string.signup_email_alert_message_loading)
    )
}

@Composable
fun SignUpEmailTitleLayout(
    title: String = "",
    subTitle: String = "",
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = title,
        style = DayoTheme.typography.h1.copy(color = Dark),
    )

    // SubTitle 영역
    AnimatedVisibility(visible = subTitle.isNotBlank()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        Text(
            text = subTitle,
            style = DayoTheme.typography.b6.copy(color = Gray2_767B83),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SignUpEmailScreen(
    context: Context = LocalContext.current,
    hideKeyboard: () -> Unit = {},
    onBackClick: () -> Unit = {},
    requestIsEmailDuplicate: (email: String) -> Unit = {},
    onCertificateEmailClick: (email: String) -> Unit = {},
    requestIsNicknameDuplicate: (nickname: String) -> Unit = {},
    onProfileUpdateClick: (nickname: String, profileImg: Bitmap?) -> Unit = { _, _ -> },
    onSignUpEmailClick: (email: String, nickname: String, password: String, profileImg: Bitmap?) -> Unit = { _, _, _, _ -> },
    isEmailDuplicate: Status = Status.LOADING,
    certificateEmailAuthCode: String = EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString(),
    isNicknameDuplicate: Boolean = false,
    profileImg: Bitmap? = null,
    setProfileImg: (Bitmap?) -> Unit = {},
    signUpStatus: Status? = null,
    updateProfileStatus: Status? = null,
    startSignUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    signUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    setSignUpStep: (SignUpStep) -> Unit = {},
) {
    val isNextButtonEnabled = remember { mutableStateOf(false) }
    val isNextButtonClickable = remember { mutableStateOf(false) }

    // Email
    val emailState = remember { mutableStateOf("") }
    val emailCertificationState =
        remember { mutableStateOf(EmailCertificationState.BEFORE_CERTIFICATION) }
    val emailCertificateCodeState = remember { mutableStateOf("") }
    val isEmailCertificateError: MutableState<Boolean?> = remember { mutableStateOf(null) }

    // Password
    val passwordState = remember { mutableStateOf("") }
    val isPasswordPassFormatError = remember { mutableStateOf(false) }
    val passwordConfirmState = remember { mutableStateOf("") }
    val isPasswordMatchError = remember { mutableStateOf(false) }

    // Nickname
    val nicknameState = remember { mutableStateOf("") }
    val nicknameCertificationState =
        remember { mutableStateOf(NicknameCertificationState.BEFORE_CERTIFICATION) }

    LaunchedEffect(signUpStatus, updateProfileStatus) {
        if (signUpStep == SignUpStep.PROFILE_SETUP &&
            (signUpStatus == Status.SUCCESS || updateProfileStatus == Status.SUCCESS)
        ) {
            setSignUpStep(SignUpStep.SIGNUP_COMPLETE)
        }
    }

    LaunchedEffect(emailState.value, isEmailDuplicate) {
        emailCertificationState.value = when {
            emailState.value.isBlank() -> {
                EmailCertificationState.BEFORE_CERTIFICATION
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailState.value).matches() -> {
                EmailCertificationState.INVALID_FORMAT
            }

            isEmailDuplicate == Status.ERROR -> {
                EmailCertificationState.DUPLICATE_EMAIL
            }

            isEmailDuplicate == Status.LOADING -> {
                EmailCertificationState.IN_PROGRESS_CHECK_EMAIL
            }

            isEmailDuplicate == Status.SUCCESS -> {
                EmailCertificationState.SUCCESS_CHECK_EMAIL
            }

            else -> emailCertificationState.value
        }
    }

    LaunchedEffect(isNicknameDuplicate, nicknameState.value) {
        nicknameCertificationState.value = when {
            nicknameState.value.isBlank() -> {
                NicknameCertificationState.BEFORE_CERTIFICATION
            }

            !Regex(NICKNAME_PERMIT_FORMAT).matches(nicknameState.value) -> {
                NicknameCertificationState.INVALID_FORMAT
            }

            isNicknameDuplicate -> {
                NicknameCertificationState.DUPLICATE_NICKNAME
            }

            !isNicknameDuplicate -> {
                NicknameCertificationState.SUCCESS
            }

            else -> nicknameCertificationState.value
        }
    }

    SignUpEmailScaffold(
        context = context,
        startSignUpStep = startSignUpStep,
        signUpStep = signUpStep,
        onBackClick = {
            when (signUpStep) {
                SignUpStep.EMAIL_VERIFICATION, SignUpStep.PASSWORD_INPUT -> {
                    emailState.value = ""
                    emailCertificationState.value =
                        EmailCertificationState.BEFORE_CERTIFICATION
                    emailCertificateCodeState.value = ""
                    isEmailCertificateError.value = null
                    passwordState.value = ""
                    setSignUpStep(SignUpStep.EMAIL_INPUT)
                }

                SignUpStep.PASSWORD_CONFIRM, SignUpStep.PROFILE_SETUP -> {
                    passwordState.value = ""
                    isPasswordPassFormatError.value = false
                    passwordConfirmState.value = ""
                    isPasswordMatchError.value = false
                    nicknameState.value = ""
                    nicknameCertificationState.value =
                        NicknameCertificationState.BEFORE_CERTIFICATION
                    setProfileImg(null)
                    setSignUpStep(SignUpStep.PASSWORD_INPUT)
                }

                SignUpStep.EMAIL_INPUT, SignUpStep.SIGNUP_COMPLETE -> {
                    onBackClick()
                }
            }
        },
        title = when (signUpStep) {
            SignUpStep.EMAIL_INPUT, SignUpStep.EMAIL_VERIFICATION -> stringResource(R.string.sign_up_email_set_address_title)
            SignUpStep.PASSWORD_INPUT, SignUpStep.PASSWORD_CONFIRM -> stringResource(R.string.sign_up_email_set_password_title)
            else -> ""
        },
        subTitle = when (signUpStep) {
            SignUpStep.EMAIL_VERIFICATION ->
                stringResource(R.string.sign_up_email_set_address_subtitle)

            else -> ""
        },
        isNextButtonEnabled = isNextButtonEnabled.value,
        isNextButtonClickable = isNextButtonClickable.value,
        onNextClick = {
            hideKeyboard()
            isNextButtonEnabled.value = false
            when (signUpStep) {
                SignUpStep.EMAIL_INPUT -> {
                    onCertificateEmailClick(emailState.value)
                    setSignUpStep(SignUpStep.EMAIL_VERIFICATION)
                }

                SignUpStep.EMAIL_VERIFICATION -> {
                    if (certificateEmailAuthCode != EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString() &&
                        certificateEmailAuthCode != SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString()
                    ) {
                        if (certificateEmailAuthCode == emailCertificateCodeState.value) {
                            setSignUpStep(SignUpStep.PASSWORD_INPUT)
                        } else {
                            isEmailCertificateError.value = true
                        }
                    }
                }

                SignUpStep.PASSWORD_INPUT -> {
                    val passwordFormat = Regex(PASSWORD_PERMIT_FORMAT)
                    if (passwordFormat.matches(passwordState.value)) {
                        setSignUpStep(SignUpStep.PASSWORD_CONFIRM)
                    } else {
                        isPasswordPassFormatError.value = true
                    }
                }

                SignUpStep.PASSWORD_CONFIRM -> {
                    if (passwordState.value == passwordConfirmState.value) {
                        setSignUpStep(SignUpStep.PROFILE_SETUP)
                    } else {
                        isPasswordMatchError.value = true
                    }
                }

                SignUpStep.PROFILE_SETUP -> {
                    if (startSignUpStep == SignUpStep.PROFILE_SETUP) {
                        onProfileUpdateClick(nicknameState.value, profileImg)
                    } else {
                        onSignUpEmailClick(
                            emailState.value,
                            nicknameState.value,
                            passwordState.value,
                            profileImg
                        )
                    }
                }

                SignUpStep.SIGNUP_COMPLETE -> Unit
            }
        }
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
        )
        when (signUpStep) {
            SignUpStep.EMAIL_INPUT -> {
                SetEmailView(
                    context = context,
                    isNextButtonEnabled = isNextButtonEnabled.value,
                    setNextButtonEnabled = { isNextButtonEnabled.value = it },
                    isNextButtonClickable = isNextButtonClickable.value,
                    setIsNextButtonClickable = { isNextButtonClickable.value = it },
                    email = emailState.value,
                    setEmail = { emailState.value = it },
                    emailCertification = emailCertificationState.value,
                    requestEmailCertification = { requestIsEmailDuplicate(it) }
                )
            }

            SignUpStep.EMAIL_VERIFICATION -> {
                SetEmailCertificationView(
                    timeOutSeconds = SIGN_UP_EMAIL_CERTIFICATE_AUTH_TIME_OUT,
                    signUpStep = signUpStep,
                    isNextButtonEnabled = isNextButtonEnabled.value,
                    setNextButtonEnabled = { isNextButtonEnabled.value = it },
                    isNextButtonClickable = isNextButtonClickable.value,
                    setIsNextButtonClickable = { isNextButtonClickable.value = it },
                    email = emailState.value,
                    emailCertification = emailCertificationState.value,
                    certificationCode = certificateEmailAuthCode,
                    certificationInputCode = emailCertificateCodeState.value,
                    setCertificationInputCode = { emailCertificateCodeState.value = it },
                    isEmailCertificateError = isEmailCertificateError.value,
                    setIsEmailCertificateError = { isEmailCertificateError.value = it },
                    requestEmailCertification = { onCertificateEmailClick(it) }
                )
            }

            SignUpStep.PASSWORD_INPUT, SignUpStep.PASSWORD_CONFIRM -> {
                SetPasswordView(
                    passwordInputViewCondition = signUpStep == SignUpStep.PASSWORD_INPUT,
                    passwordConfirmationViewCondition = signUpStep == SignUpStep.PASSWORD_CONFIRM,
                    isNextButtonEnabled = isNextButtonEnabled.value,
                    setNextButtonEnabled = { isNextButtonEnabled.value = it },
                    isNextButtonClickable = isNextButtonClickable.value,
                    setIsNextButtonClickable = { isNextButtonClickable.value = it },
                    password = passwordState.value,
                    setPassword = { passwordState.value = it },
                    isPasswordFormatValid = !isPasswordPassFormatError.value,
                    setIsPasswordFormatValid = { isPasswordPassFormatError.value = !it },
                    passwordConfirmation = passwordConfirmState.value,
                    setPasswordConfirmation = { passwordConfirmState.value = it },
                    isPasswordMismatch = isPasswordMatchError.value,
                    setIsPasswordMismatch = { isPasswordMatchError.value = it }
                )
            }

            SignUpStep.PROFILE_SETUP -> {
                SetProfileSetupView(
                    isNextButtonEnabled = isNextButtonEnabled,
                    isNextButtonClickable = isNextButtonClickable,
                    nicknameState = nicknameState,
                    nicknameCertificationState = nicknameCertificationState,
                    requestIsNicknameDuplicate = requestIsNicknameDuplicate,
                    profileImg = profileImg
                )
            }

            SignUpStep.SIGNUP_COMPLETE -> {
                SignUpCompleteScreen(nickname = nicknameState.value)
            }
        }
    }
}

@Composable
fun SignUpEmailScaffold(
    context: Context = LocalContext.current,
    startSignUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    signUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    onBackClick: () -> Unit = {},
    title: String = "",
    subTitle: String = "",
    isNextButtonEnabled: Boolean = false,
    isNextButtonClickable: Boolean = false,
    onNextClick: () -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit),
) {
    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    if ((signUpStep != SignUpStep.SIGNUP_COMPLETE)) {
                        NoRippleIconButton(
                            onClick = { onBackClick() },
                            iconContentDescription = stringResource(R.string.back_sign),
                            iconPainter = painterResource(id = R.drawable.ic_arrow_left),
                        )
                    }
                },
                rightIcon = {
                    if (signUpStep == SignUpStep.SIGNUP_COMPLETE) {
                        NoRippleIconButton(
                            onClick = {
                                if (startSignUpStep == SignUpStep.PROFILE_SETUP) {
                                    navigateToHome(context = context)
                                } else {
                                    onBackClick()
                                }
                            },
                            iconContentDescription = stringResource(R.string.close_sign),
                            iconPainter = painterResource(id = R.drawable.ic_x),
                        )
                    }
                },
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
                    .padding(horizontal = 20.dp, vertical = 0.dp)
                    .fillMaxWidth()
                    .wrapContentSize()
            ) {
                if (signUpStep.stepNum <= SignUpStep.PASSWORD_CONFIRM.stepNum) {
                    SignUpEmailTitleLayout(title = title, subTitle = subTitle)
                }
                content()
            }

            if (signUpStep != SignUpStep.SIGNUP_COMPLETE) {
                Spacer(modifier = Modifier.weight(1f))
                SignUpEmailBottomLayout(
                    signUpStep = signUpStep,
                    onNextClick = { onNextClick() },
                    isSignUpButtonEnabled = isNextButtonEnabled,
                    isSignUpButtonClickable = isNextButtonClickable,
                )
            }
        }
    }
}

@Composable
fun SignUpEmailBottomLayout(
    signUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    onNextClick: () -> Unit = {},
    isSignUpButtonEnabled: Boolean,
    isSignUpButtonClickable: Boolean
) {
    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        SignUpEmailNextButton(
            signUpStep = signUpStep,
            onButtonClick = { onNextClick() },
            isSignUpButtonEnabled = isSignUpButtonEnabled,
            isSignUpButtonClickable = isSignUpButtonClickable
        )
    }
}

@Composable
@Preview
fun SignUpEmailNextButton(
    signUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    onButtonClick: () -> Unit = {},
    isSignUpButtonEnabled: Boolean = false,
    isSignUpButtonClickable: Boolean = false,
) {
    if (signUpStep == SignUpStep.SIGNUP_COMPLETE) return

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
            label = when (signUpStep) {
                SignUpStep.EMAIL_INPUT -> stringResource(R.string.sign_up_email_set_address_next_button)
                SignUpStep.EMAIL_VERIFICATION -> stringResource(R.string.sign_up_email_set_address_certification_next_button)
                SignUpStep.PASSWORD_INPUT -> stringResource(R.string.sign_up_email_set_password_next_button)
                SignUpStep.PASSWORD_CONFIRM -> stringResource(R.string.sign_up_email_set_password_confirm_next_button)
                SignUpStep.PROFILE_SETUP -> stringResource(R.string.sign_up_email_set_profile_next_button)
                else -> ""
            },
            textStyle = DayoTheme.typography.b3.copy(color = White_FFFFFF),
            onClick = { if (isSignUpButtonClickable) onButtonClick() },
            enabled = isSignUpButtonEnabled,
        )
    }
}