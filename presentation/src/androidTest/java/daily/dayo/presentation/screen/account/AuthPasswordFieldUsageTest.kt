package daily.dayo.presentation.screen.account

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import daily.dayo.presentation.theme.DayoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthPasswordFieldUsageTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun assertContentDescriptionCount(contentDescription: String, expectedCount: Int) {
        composeRule.onAllNodesWithContentDescription(contentDescription)
            .assertCountEquals(expectedCount)
    }

    @Test
    fun signInEmailInputLayout_showsClearAndVisibilityOnEditablePasswordField() {
        var email by mutableStateOf("")
        var password by mutableStateOf("password")

        composeRule.setContent {
            DayoTheme {
                SignInEmailInputLayout(
                    emailValue = email,
                    onEmailChange = { email = it },
                    passwordValue = password,
                    onPasswordChange = { password = it }
                )
            }
        }

        assertContentDescriptionCount("Clear password", 1)
        assertContentDescriptionCount("Show password", 1)
    }

    @Test
    fun signUpPasswordConfirmLayout_hidesClearOnDisabledReferenceField() {
        var password by mutableStateOf("password")
        var passwordConfirmation by mutableStateOf("confirm")

        composeRule.setContent {
            DayoTheme {
                SetPasswordView(
                    passwordInputViewCondition = false,
                    passwordConfirmationViewCondition = true,
                    password = password,
                    setPassword = { password = it },
                    isPasswordFormatValid = true,
                    passwordConfirmation = passwordConfirmation,
                    setPasswordConfirmation = { passwordConfirmation = it }
                )
            }
        }

        assertContentDescriptionCount("Clear password", 1)
        assertContentDescriptionCount("Show password", 2)
    }

    @Test
    fun resetPasswordConfirmLayout_hidesClearOnDisabledReferenceField() {
        var password by mutableStateOf("password")
        var passwordConfirmation by mutableStateOf("confirm")

        composeRule.setContent {
            DayoTheme {
                NewPasswordLayout(
                    resetPasswordStep = ResetPasswordStep.NEW_PASSWORD_CONFIRM,
                    password = password,
                    setPassword = { password = it },
                    isPasswordFormatValid = true,
                    passwordConfirmation = passwordConfirmation,
                    setPasswordConfirmation = { passwordConfirmation = it }
                )
            }
        }

        assertContentDescriptionCount("Clear password", 1)
        assertContentDescriptionCount("Show password", 2)
    }
}
