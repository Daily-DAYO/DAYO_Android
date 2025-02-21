package daily.dayo.presentation.screen.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.account.model.EmailCertificationState
import daily.dayo.presentation.screen.account.model.SignUpStep
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.DayoTimerTextField
import daily.dayo.presentation.viewmodel.AccountViewModel

@Preview
@Composable
fun SetEmailCertificationView(
    timeOutSeconds: Int = 60,
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
    val seconds = remember { mutableStateOf(timeOutSeconds) }
    val timerErrorMessageRedId =
        remember { mutableStateOf((R.string.sign_up_email_set_address_certification_fail_wrong)) }
    val isTimeOut = remember { mutableStateOf(false) }

    setNextButtonEnabled(
        certificateEmailAuthCodeFormat.matches(certificationInputCode) && !isTimeOut.value
    )
    setIsNextButtonClickable(
        certificateEmailAuthCodeFormat.matches(certificationInputCode) && !isTimeOut.value
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
            onTimeOut = {
                isTimeOut.value = true
                setNextButtonEnabled(false)
                setIsNextButtonClickable(false)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
    )
    if (certificationCode != AccountViewModel.EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString()) {
        // 네트워크 오류로 정상적으로 비교할 인증코드를 보내지 못한 경우 에러 처리
        if (certificationCode == AccountViewModel.SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString()) {
            timerErrorMessageRedId.value =
                R.string.sign_up_email_set_address_certification_fail_network
            setIsEmailCertificateError(true)
        }
    }

    AnimatedVisibility(
        visible = certificationCode != AccountViewModel.SIGN_UP_EMAIL_CERTIFICATE_AUTH_CODE_FAIL.toString() &&
                certificationCode != AccountViewModel.EMAIL_CERTIFICATE_AUTH_CODE_INITIAL.toString(),
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
                    isTimeOut.value = false
                    requestEmailCertification(email)
                },
                underline = true,
                textStyle = DayoTheme.typography.b6.copy(Gray2_767B83),
            )
        }
    }
}