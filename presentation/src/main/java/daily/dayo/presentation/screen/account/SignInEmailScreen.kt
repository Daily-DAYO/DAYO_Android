package daily.dayo.presentation.screen.account

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoPasswordTextField
import daily.dayo.presentation.view.DayoTextField
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.Loading
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.AccountViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun SignInEmailRoute(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit = {},
    navigateToFindPassword: () -> Unit = {},
    navigateToSignUpEmail: () -> Unit = {},
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isInitialized by remember { mutableStateOf(false) }
    val signInSuccess by accountViewModel.signInSuccess.collectAsState()

    LaunchedEffect(signInSuccess) {
        if (!isInitialized) {
            accountViewModel.initializeSignInSuccess()
            isInitialized = true
        }

        when (signInSuccess) {
            Status.SUCCESS -> {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
                (context as Activity).finish()
            }

            Status.ERROR -> {
                snackBarHostState.showSnackbar(
                    ContextCompat.getString(context, R.string.sign_in_email_fail_message)
                )
            }

            Status.LOADING -> {}
            null -> {

            }
        }
    }

    SignInEmailScreen(
        onBackClick = onBackClick,
        onForgetPasswordClick = navigateToFindPassword,
        onSignUpClick = navigateToSignUpEmail,
        onSignInClick = { email, password ->
            accountViewModel.requestSignInEmail(
                email = email,
                password = password
            )
        },
        accountViewModel = accountViewModel
    )

    Loading(
        isVisible = (signInSuccess == Status.LOADING || signInSuccess == Status.SUCCESS),
        lottieFile = R.raw.dayo_loading,
        lottieModifier = Modifier
            .width(82.dp)
            .height(85.dp)
    )
}

@Composable
@Preview
fun SignInEmailScreen(
    onBackClick: () -> Unit = {},
    onForgetPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onSignInClick: (email: String, password: String) -> Unit = { _, _ -> },
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isSignInButtonEnabled = remember(emailState.value, passwordState.value) {
        emailState.value.isNotBlank() && passwordState.value.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DayoTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top
    ) {
        SignInEmailActionbarLayout(onBackClick = onBackClick)
        Column(
            modifier = Modifier
val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 0.dp)
        ) {
            SignInEmailActionbarLayout(onBackClick = onBackClick)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 20.dp, vertical = 0.dp)
            ) {
                SignInEmailTitle()
                SignInEmailInputLayout(
                    emailValue = emailState.value,
                    onEmailChange = { emailState.value = it },
                    passwordValue = passwordState.value,
                    onPasswordChange = { passwordState.value = it },
                    onForgetPasswordClick = onForgetPasswordClick,
                    onSignInClick = { onSignInClick(emailState.value, passwordState.value) }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SignInEmailBottomLayout(
                onSignUpClick = onSignUpClick,
                onSignInClick = { onSignInClick(emailState.value, passwordState.value) },
                isSignInButtonEnabled = isSignInButtonEnabled
            )
        }
    }
}
                .fillMaxWidth()
                .wrapContentSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            SignInEmailTitle()
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            SignInEmailInputLayout(
                emailValue = emailState.value,
                onEmailChange = { emailState.value = it },
                passwordValue = passwordState.value,
                onPasswordChange = { passwordState.value = it },
                onForgetPasswordClick = onForgetPasswordClick,
                onSignInClick = { onSignInClick(emailState.value, passwordState.value) }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        SignInEmailBottomLayout(
            onSignUpClick = onSignUpClick,
            onSignInClick = { onSignInClick(emailState.value, passwordState.value) },
            isSignInButtonEnabled = isSignInButtonEnabled
        )
    }
}

@Composable
@Preview
fun SignInEmailActionbarLayout(onBackClick: () -> Unit = {}) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = {
                    onBackClick()
                },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        }
    )
}

@Composable
@Preview
fun SignInEmailTitle() {
    Text(
        text = stringResource(R.string.sign_in_email_title),
        style = DayoTheme.typography.h1.copy(color = Dark),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
@Preview
fun SignInEmailInputLayout(
    emailValue: String = "",
    onEmailChange: (String) -> Unit = {},
    passwordValue: String = "",
    onPasswordChange: (String) -> Unit = {},
    onForgetPasswordClick: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    val focusRequesterEmail = FocusRequester()
    val focusRequesterPassword = FocusRequester()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        DayoTextField(
            modifier = Modifier.focusRequester(focusRequesterEmail),
            label = stringResource(R.string.sign_in_email_input_email_title),
            placeholder = stringResource(R.string.sign_in_email_input_email_title),
            value = emailValue,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email).copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
        DayoPasswordTextField(
            modifier = Modifier.focusRequester(focusRequesterPassword),
            label = stringResource(R.string.sign_in_email_input_password_title),
            placeholder = stringResource(R.string.sign_in_email_input_password_placeholder),
            value = passwordValue,
            onValueChange = onPasswordChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password).copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSignInClick() }),
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
        Text(
            text = stringResource(R.string.sign_in_email_forget_password),
            style = DayoTheme.typography.caption4.copy(color = Gray1_50545B),
            modifier = Modifier
                .padding(horizontal = 2.dp, vertical = 8.dp)
                .wrapContentSize()
                .align(Alignment.End)
                .clickableSingle { onForgetPasswordClick() },
            textAlign = TextAlign.End,
        )
    }
}

@Composable
@Preview
fun SignInEmailBottomLayout(
    onSignUpClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    isSignInButtonEnabled: Boolean = false
) {
    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        SignInEmailSignUpLayout(onSignUpClick = onSignUpClick)
        SignInEmailButton(
            onSignInClick = onSignInClick,
            isSignInButtonEnabled = isSignInButtonEnabled
        )
    }
}

@Composable
@Preview
fun SignInEmailSignUpLayout(
    onSignUpClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = stringResource(R.string.sign_up_email),
            style = DayoTheme.typography.b5.copy(color = Gray3_9FA5AE),
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally)
                .clickableSingle { onSignUpClick() },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun SignInEmailButton(
    onSignInClick: () -> Unit = {},
    isSignInButtonEnabled: Boolean = false
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
            label = stringResource(R.string.sign_in),
            textStyle = DayoTheme.typography.b3.copy(color = White_FFFFFF),
            onClick = { onSignInClick() },
            enabled = isSignInButtonEnabled
        )
    }
}