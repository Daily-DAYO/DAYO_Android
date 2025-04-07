package daily.dayo.presentation.screen.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.common.ReplaceUnicode
import daily.dayo.presentation.screen.account.model.SignUpStep
import daily.dayo.presentation.view.DayoPasswordTextField

@Composable
@Preview
fun SetPasswordView(
    passwordInputViewCondition: Boolean = true,
    passwordConfirmationViewCondition: Boolean = false,
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
            if (passwordInputViewCondition) password.isNotBlank()
            else passwordConfirmation.isNotBlank()
        )
        setIsNextButtonClickable(
            if (passwordInputViewCondition) password.isNotBlank()
            else passwordConfirmation.isNotBlank()
        )

        AnimatedVisibility(
            visible = passwordConfirmationViewCondition,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            DayoPasswordTextField(
                value = passwordConfirmation,
                onValueChange = {
                    val trimmedText = ReplaceUnicode.trimBlankText(it)
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

        if (passwordConfirmationViewCondition) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
        }

        AnimatedVisibility(
            visible = passwordInputViewCondition || passwordConfirmationViewCondition,
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
                isError = if (passwordInputViewCondition) !isPasswordFormatValid else false,
                errorMessage = stringResource(R.string.sign_up_email_set_password_message_format_fail),
                isEnabled = passwordInputViewCondition,
            )
        }
    }
}