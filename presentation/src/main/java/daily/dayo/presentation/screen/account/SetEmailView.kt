package daily.dayo.presentation.screen.account

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.account.model.EmailCertificationState
import daily.dayo.presentation.view.DayoTextField

@Composable
fun SetEmailView(
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
            if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
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