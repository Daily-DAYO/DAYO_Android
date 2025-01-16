package daily.dayo.presentation.screen.account

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import daily.dayo.presentation.R
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.common.image.ImageResizeUtil.USER_PROFILE_THUMBNAIL_RESIZE_SIZE
import daily.dayo.presentation.common.image.ImageResizeUtil.resizeBitmap
import daily.dayo.presentation.common.image.launchCamera
import daily.dayo.presentation.common.image.launchGallery
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.BadgeRoundImageView
import daily.dayo.presentation.view.DayoPasswordTextField
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.DayoTextField
import daily.dayo.presentation.view.DayoTimerTextField
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.Loading
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.BottomSheetDialog
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.AccountViewModel.Companion.SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL
import daily.dayo.presentation.viewmodel.AccountViewModel.Companion.EMAIL_CERTIFICATE_AUTH_CODE_INITIAL
import daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val SIGN_UP_EMAIL_CERTIFICATE_AUTH_TIME_OUT = 180
const val NICKNAME_PERMIT_FORMAT = "^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]{2,10}$"

@OptIn(ExperimentalMaterialApi::class)
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
    val bottomSheetState = getBottomSheetDialogState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val bitmapOptions =
        BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }

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
                        val bitmap = BitmapFactory.decodeStream(it, null, bitmapOptions)
                        val resizedBitmap = bitmap?.let {
                            resizeBitmap(
                                it,
                                USER_PROFILE_THUMBNAIL_RESIZE_SIZE,
                                USER_PROFILE_THUMBNAIL_RESIZE_SIZE
                            )
                        }
                        resizedBitmap?.let {
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
            if (bitmap != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    resizeBitmap(
                        bitmap,
                        USER_PROFILE_THUMBNAIL_RESIZE_SIZE,
                        USER_PROFILE_THUMBNAIL_RESIZE_SIZE
                    ).let {
                        profileImgState.value = it
                    }
                }
            }
        },
        onPermissionDenied = {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.permission_fail_message_camera))
            }
        }
    )

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
        coroutineScope = coroutineScope,
        bottomSheetState = bottomSheetState,
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
        onClickProfileSelect = {
            coroutineScope.launch {
                showProfileGallery = true
                bottomSheetState.hide()
            }
        },
        onClickProfileCapture = {
            coroutineScope.launch {
                showProfileCapture = true
                bottomSheetState.hide()
            }
        },
        onClickProfileReset = {
            profileImgState.value = null
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        },
        onProfileUpdateClick = { nickname, profileImg ->
            val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss-SSS", Locale.KOREA)
            val fileName =
                imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() + ".jpg"

            coroutineScope.launch {
                profileSettingViewModel.requestUpdateMyProfileWithResizedFile(
                    nickname = nickname,
                    profileImg = profileImg,
                    profileImgTempDir = "${context.cacheDir}/$fileName",
                    isReset = profileImg == null
                )
            }

        },
        onSignUpEmailClick = { email, nickname, password, profileImg ->
            val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss-SSS", Locale.KOREA)
            val fileName =
                imageFileTimeFormat.format(Date(System.currentTimeMillis())).toString() + ".jpg"

            coroutineScope.launch {
                accountViewModel.requestSignupEmail(
                    email = email,
                    nickname = nickname,
                    password = password,
                    profileImg = profileImg,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun SignUpEmailScreen(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    bottomSheetState: ModalBottomSheetState = getBottomSheetDialogState(),
    hideKeyboard: () -> Unit = {},
    onBackClick: () -> Unit = {},
    requestIsEmailDuplicate: (email: String) -> Unit = {},
    onCertificateEmailClick: (email: String) -> Unit = {},
    requestIsNicknameDuplicate: (nickname: String) -> Unit = {},
    onClickProfileSelect: () -> Unit = {},
    onClickProfileCapture: () -> Unit = {},
    onClickProfileReset: () -> Unit = {},
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
                    !Regex(NICKNAME_PERMIT_FORMAT).matches(nicknameState.value) -> {
                        NicknameCertificationState.INVALID_FORMAT
                    }

                    isNicknameDuplicate -> {
                        NicknameCertificationState.DUPLICATE_NICKNAME
                    }

                    else -> {
                        NicknameCertificationState.BEFORE_CERTIFICATION
                    }
                }
            }

            SignUpEmailActionbarLayout(
                context = context,
                onBackClick = onBackClick,
                startSignUpStep = startSignUpStep,
                signUpStep = signUpStep,
                backStep = {
                    if (signUpStep != SignUpStep.EMAIL_INPUT) {
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

                            else -> {}
                        }
                    }
                })
            Column(
                modifier = Modifier
                    .background(DayoTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 0.dp)
                    .fillMaxWidth()
                    .wrapContentSize()
            ) {
                if (signUpStep.stepNum <= SignUpStep.PASSWORD_CONFIRM.stepNum) {
                    // Title 영역
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (signUpStep) {
                            SignUpStep.EMAIL_INPUT, SignUpStep.EMAIL_VERIFICATION ->
                                stringResource(R.string.sign_up_email_set_address_title)

                            SignUpStep.PASSWORD_INPUT, SignUpStep.PASSWORD_CONFIRM ->
                                stringResource(R.string.sign_up_email_set_password_title)

                            else -> ""
                        },
                        style = DayoTheme.typography.h1.copy(color = Dark),
                    )

                    // SubTitle 영역
                    AnimatedVisibility(
                        visible = signUpStep == SignUpStep.EMAIL_VERIFICATION,
                    ) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        )
                        Text(
                            text = stringResource(R.string.sign_up_email_set_address_subtitle),
                            style = DayoTheme.typography.b6.copy(color = Gray2_767B83),
                        )
                    }
                }

                // Contents 영역
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                )
                when (signUpStep) {
                    SignUpStep.EMAIL_INPUT -> {
                        EmailInputScreen(
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
                        EmailCertificationScreen(
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
                        PasswordScreen(
                            signUpStep = signUpStep,
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
                        val placeholder = remember {
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_profile_default_user_profile
                            )
                        }
                        val profileImageClickModifier = Modifier
                            .size(110.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(percent = 50))
                            .clickableSingle(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    coroutineScope.launch { bottomSheetState.show() }
                                }
                            )

                        BadgeRoundImageView(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            context = context,
                            imageUrl = profileImg ?: "",
                            imageDescription = "Set Profile image",
                            placeholder = placeholder,
                            roundSize = 55.dp,
                            contentModifier = profileImageClickModifier
                        )

                        ProfileSetupScreen(
                            context = context,
                            coroutineScope = coroutineScope,
                            bottomSheetState = bottomSheetState,
                            signUpStep = signUpStep,
                            isNextButtonEnabled = isNextButtonEnabled.value,
                            setNextButtonEnabled = { isNextButtonEnabled.value = it },
                            isNextButtonClickable = isNextButtonClickable.value,
                            setIsNextButtonClickable = { isNextButtonClickable.value = it },
                            nickname = nicknameState.value,
                            setNickname = { nicknameState.value = it },
                            nicknameCertification = nicknameCertificationState.value,
                            setNicknameCertification = { nicknameCertificationState.value = it },
                            requestIsNicknameDuplicate = { requestIsNicknameDuplicate(it) },
                            profileImg = profileImg,
                            setProfileImg = { setProfileImg(it) }
                        )
                    }

                    SignUpStep.SIGNUP_COMPLETE -> {
                        CompleteScreen(nickname = nicknameState.value)
                    }
                }
            }

            if (signUpStep != SignUpStep.SIGNUP_COMPLETE) {
                Spacer(modifier = Modifier.weight(1f))
                SignUpEmailBottomLayout(
                    signUpStep = signUpStep,
                    onNextClick = {
                        hideKeyboard()
                        isNextButtonEnabled.value = false
                        when (signUpStep) {
                            SignUpStep.EMAIL_INPUT -> {
                                onCertificateEmailClick(emailState.value)
                                setSignUpStep(SignUpStep.EMAIL_VERIFICATION)
                            }

                            SignUpStep.EMAIL_VERIFICATION -> {
                                if (certificateEmailAuthCode == EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString() ||
                                    certificateEmailAuthCode == SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString()
                                ) {
                                    // 인증코드가 없으므로 검증할 필요도 없음
                                } else {
                                    if (certificateEmailAuthCode == emailCertificateCodeState.value) {
                                        setSignUpStep(SignUpStep.PASSWORD_INPUT)
                                    } else {
                                        isEmailCertificateError.value = true
                                    }
                                }
                            }

                            SignUpStep.PASSWORD_INPUT -> {
                                val passwordFormat = Regex("^[a-z0-9]{8,16}$")
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
                                    onProfileUpdateClick(
                                        nicknameState.value,
                                        profileImg
                                    )
                                } else {
                                    onSignUpEmailClick(
                                        emailState.value,
                                        nicknameState.value,
                                        passwordState.value,
                                        profileImg
                                    )
                                }
                            }

                            SignUpStep.SIGNUP_COMPLETE -> {}
                        }
                    },
                    isSignUpButtonEnabled = isNextButtonEnabled.value,
                    isSignUpButtonClickable = isNextButtonClickable.value,
                )
            }
        }
        ProfileImageBottomSheetDialog(
            bottomSheetState,
            onClickProfileSelect,
            onClickProfileCapture,
            onClickProfileReset
        )
    }
}

@Composable
fun SignUpEmailActionbarLayout(
    context: Context = LocalContext.current,
    onBackClick: () -> Unit = {},
    startSignUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    signUpStep: SignUpStep = SignUpStep.EMAIL_INPUT,
    backStep: () -> Unit = {}
) {
    BackHandler {
        if (signUpStep == SignUpStep.EMAIL_INPUT || startSignUpStep == SignUpStep.PROFILE_SETUP) {
            onBackClick()
        } else {
            backStep()
        }
    }

    TopNavigation(
        leftIcon = {
            if (signUpStep != SignUpStep.SIGNUP_COMPLETE && startSignUpStep != SignUpStep.PROFILE_SETUP) {
                NoRippleIconButton(
                    onClick = {
                        if (signUpStep == SignUpStep.EMAIL_INPUT || startSignUpStep == SignUpStep.PROFILE_SETUP) {
                            onBackClick()
                        } else {
                            backStep()
                        }
                    },
                    iconContentDescription = stringResource(R.string.back_sign),
                    iconPainter = painterResource(id = R.drawable.ic_arrow_left),
                )
            }
        },
        rightIcon = {
            if (signUpStep == SignUpStep.SIGNUP_COMPLETE) {
                NoRippleIconButton(
                    onClick = {
                        // FINISH
                        if (startSignUpStep == SignUpStep.PROFILE_SETUP) {
                            navigateToHome(context = context)
                        } else {
                            onBackClick()
                        }
                    },
                    iconContentDescription = stringResource(R.string.close_sign),
                    iconPainter = painterResource(id = R.drawable.ic_x_sign),
                )
            }
        },
    )
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

@Composable
private fun EmailInputScreen(
    context: Context = LocalContext.current,
    isNextButtonEnabled: Boolean = false,
    setNextButtonEnabled: (Boolean) -> Unit = {},
    isNextButtonClickable: Boolean = false,
    setIsNextButtonClickable: (Boolean) -> Unit = {},
    email: String = "",
    setEmail: (String) -> Unit = {},
    emailCertification: EmailCertificationState = EmailCertificationState.BEFORE_CERTIFICATION,
    requestEmailCertification: (String) -> Unit = {},
) {
    val lastErrorMessage = remember { mutableStateOf("") }

    // text를 입력할때마다 button enable 상태가 반경되는 것은 방지하되, 가능한 이메일 상태 -> 불가능으로 변경하는 경우에 다음으로 넘어가지 않도록 clickable은 조정
    setNextButtonEnabled(
        emailCertification == EmailCertificationState.SUCCESS_CHECK_EMAIL
                || (isNextButtonEnabled && emailCertification == EmailCertificationState.IN_PROGRESS_CHECK_EMAIL)
    )
    setIsNextButtonClickable(emailCertification == EmailCertificationState.SUCCESS_CHECK_EMAIL)

    LaunchedEffect(emailCertification) {
        lastErrorMessage.value = when (emailCertification) {
            EmailCertificationState.BEFORE_CERTIFICATION -> ""
            EmailCertificationState.IN_PROGRESS_CHECK_EMAIL -> lastErrorMessage.value
            EmailCertificationState.INVALID_FORMAT -> context.getString(R.string.sign_up_email_set_address_fail_invalid_format)
            EmailCertificationState.DUPLICATE_EMAIL -> context.getString(R.string.sign_up_email_set_address_fail_duplicate)
            EmailCertificationState.SUCCESS_CHECK_EMAIL -> ""
            else -> lastErrorMessage.value
        }
    }

    DayoTextField(
        value = email,
        onValueChange = {
            setEmail(it)
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                requestEmailCertification(it)
            }
        },
        label = stringResource(R.string.email),
        placeholder = stringResource(R.string.sign_up_email_set_address_placeholder),
        trailingIconId = if (email.isNotBlank()) R.drawable.ic_trailing_check else null,
        errorTrailingIconId = R.drawable.ic_trailing_error,
        errorMessage = lastErrorMessage.value,
        isError = if (email.isBlank()) null else !isNextButtonEnabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    )
}

@Preview
@Composable
private fun EmailCertificationScreen(
    signUpStep: SignUpStep = SignUpStep.EMAIL_VERIFICATION,
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
        mutableStateOf(SIGN_UP_EMAIL_CERTIFICATE_AUTH_TIME_OUT)
    }
    val timerErrorMessageRedId =
        remember { mutableStateOf((R.string.sign_up_email_set_address_certification_fail_wrong)) }

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
            else stringResource(R.string.sign_up_email_set_address_certification_input_title),
            placeholder = if (certificationInputCode.isBlank())
                stringResource(R.string.sign_up_email_set_address_certification_input_placeholder) else "",
            isError = isEmailCertificateError ?: false,
            errorMessage = stringResource(timerErrorMessageRedId.value),
            timeOutErrorMessage = stringResource(R.string.sign_up_email_set_address_certification_fail_time_out),
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
        if (certificationCode == SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString()) {
            timerErrorMessageRedId.value =
                R.string.sign_up_email_set_address_certification_fail_network
            setIsEmailCertificateError(true)
        }
    }

    AnimatedVisibility(
        visible = certificationCode != SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString() &&
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
                text = stringResource(R.string.sign_up_email_set_address_resend_button),
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
private fun PasswordScreen(
    signUpStep: SignUpStep = SignUpStep.PASSWORD_INPUT,
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
            if (signUpStep == SignUpStep.PASSWORD_INPUT) password.isNotBlank()
            else passwordConfirmation.isNotBlank()
        )
        setIsNextButtonClickable(
            if (signUpStep == SignUpStep.PASSWORD_INPUT) password.isNotBlank()
            else passwordConfirmation.isNotBlank()
        )

        AnimatedVisibility(
            visible = signUpStep == SignUpStep.PASSWORD_CONFIRM,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            DayoPasswordTextField(
                value = passwordConfirmation,
                onValueChange = {
                    val trimmedText = trimBlankText(it)
                    setIsPasswordMismatch(false) // 입력 시, 에러메시지 제거
                    setPasswordConfirmation(it)
                },
                label = if (passwordConfirmation.isBlank()) " "
                else stringResource(R.string.sign_up_email_set_password_confirm_input_title),
                placeholder = if (passwordConfirmation.isBlank()) stringResource(R.string.sign_up_email_set_password_confirm_input_placeholder) else "",
                isError = isPasswordMismatch,
                errorMessage = stringResource(R.string.sign_up_email_set_password_confirm_fail_not_match),
            )
        }

        if (signUpStep == SignUpStep.PASSWORD_CONFIRM) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
        }

        AnimatedVisibility(
            visible = signUpStep == SignUpStep.PASSWORD_INPUT || signUpStep == SignUpStep.PASSWORD_CONFIRM,
            enter = slideInVertically(),
        ) {
            DayoPasswordTextField(
                value = password,
                onValueChange = {
                    setIsPasswordFormatValid(true) // 입력 시, 에러메시지 제거
                    setPassword(it)
                },
                label = if (password.isBlank()) " "
                else stringResource(R.string.sign_up_email_set_password_input_title),
                placeholder = if (password.isBlank())
                    stringResource(R.string.sign_up_email_set_password_input_placeholder) else "",
                isError = if (signUpStep == SignUpStep.PASSWORD_INPUT) !isPasswordFormatValid else false,
                errorMessage = stringResource(R.string.sign_up_email_set_password_message_format_fail),
                isEnabled = signUpStep == SignUpStep.PASSWORD_INPUT,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
private fun ProfileSetupScreen(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    bottomSheetState: ModalBottomSheetState = getBottomSheetDialogState(),
    signUpStep: SignUpStep = SignUpStep.PASSWORD_INPUT,
    isNextButtonEnabled: Boolean = false,
    setNextButtonEnabled: (Boolean) -> Unit = {},
    isNextButtonClickable: Boolean = false,
    setIsNextButtonClickable: (Boolean) -> Unit = {},
    nickname: String = "",
    setNickname: (String) -> Unit = {},
    nicknameCertification: NicknameCertificationState = NicknameCertificationState.BEFORE_CERTIFICATION,
    setNicknameCertification: (NicknameCertificationState) -> Unit = {},
    requestIsNicknameDuplicate: (String) -> Unit = {},
    profileImg: Bitmap? = null,
    setProfileImg: (Bitmap?) -> Unit = {},
) {
    val nicknamePermitFormatRegex = Regex(NICKNAME_PERMIT_FORMAT)
    setNextButtonEnabled(
        !(nicknameCertification == NicknameCertificationState.DUPLICATE_NICKNAME
                || nicknameCertification == NicknameCertificationState.INVALID_FORMAT)
    )
    setIsNextButtonClickable(
        !(nicknameCertification == NicknameCertificationState.DUPLICATE_NICKNAME
                || nicknameCertification == NicknameCertificationState.INVALID_FORMAT)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Column {
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
                    NicknameCertificationState.DUPLICATE_NICKNAME -> stringResource(
                        R.string.sign_up_email_set_profile_nickname_message_duplicate_fail
                    )

                    NicknameCertificationState.INVALID_FORMAT -> stringResource(R.string.sign_up_email_set_profile_nickname_message_format_fail)
                    else -> ""
                },
            )
        }
    }
}

@Preview
@Composable
private fun CompleteScreen(nickname: String = "닉네임") {
    val completeLottieSpec: LottieCompositionSpec =
        LottieCompositionSpec.RawRes(R.raw.signup_email_complete_dayo_logo)
    val completeLottie by rememberLottieComposition(completeLottieSpec)
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(0.dp, 12.dp, 0.dp, 0.dp),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = DayoTheme.typography.h1.fontWeight)) {
                    append(nickname)
                }
                append("님,\n가입을 축하드려요!")
            },
            style = DayoTheme.typography.h2.copy(color = Dark),
            textAlign = TextAlign.Center,
        )

        LottieAnimation(
            composition = completeLottie,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProfileImageBottomSheetDialog(
    bottomSheetState: ModalBottomSheetState,
    onClickProfileSelect: () -> Unit,
    onClickProfileCapture: () -> Unit,
    onClickProfileReset: () -> Unit,
) {
    BottomSheetDialog(
        sheetState = bottomSheetState,
        buttons = listOf(
            Pair(stringResource(id = R.string.image_option_gallery)) {
                onClickProfileSelect()
            }, Pair(stringResource(id = R.string.image_option_camera)) {
                onClickProfileCapture()
            }, Pair(stringResource(id = R.string.sign_up_email_set_profile_image_reset)) {
                onClickProfileReset()
            }),
        isFirstButtonColored = true
    )
}

enum class EmailCertificationState {
    BEFORE_CERTIFICATION, // 인증 전
    IN_PROGRESS_CHECK_EMAIL, // 이메일 중복/형식 확인 중
    IN_PROGRESS_CHECK_CERTIFICATION, // 인증번호 받는 중
    SUCCESS_CHECK_EMAIL,              // 인증 성공
    SUCCESS_CHECK_EMAIL_CERTIFICATION,              // 인증 성공
    NOT_EXIST_EMAIL,      // 존재하지 않는 이메일로 인증 실패
    DUPLICATE_EMAIL,      // 중복된 이메일로 인증 실패
    INVALID_FORMAT,       // 잘못된 형식의 이메일로 인증 실패
    TIMEOUT               // 인증시간 초과로 인증 실패
}

enum class NicknameCertificationState {
    BEFORE_CERTIFICATION, // 확인 전
    IN_PROGRESS_CHECK_NICKNAME, // 닉네임 중복 확인 중
    SUCCESS,              // 중복되지 않은 닉네임
    DUPLICATE_NICKNAME,   // 중복된 닉네임
    INVALID_FORMAT        // 잘못된 형식의 닉네임
}

enum class SignUpStep(val stepNum: Int) {
    EMAIL_INPUT(1),           // 이메일 주소 입력
    EMAIL_VERIFICATION(2),    // 인증번호 입력
    PASSWORD_INPUT(3),        // 비밀번호 입력
    PASSWORD_CONFIRM(4),      // 비밀번호 재입력
    PROFILE_SETUP(5),         // 프로필 설정 (사진 및 닉네임)
    SIGNUP_COMPLETE(6)        // 회원가입 완료
}