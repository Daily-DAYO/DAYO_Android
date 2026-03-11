package daily.dayo.presentation.view

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import daily.dayo.presentation.theme.DayoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DayoPasswordTextFieldTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun assertContentDescriptionCount(contentDescription: String, expectedCount: Int) {
        composeRule.onAllNodesWithContentDescription(contentDescription)
            .assertCountEquals(expectedCount)
    }

    @Test
    fun givenTextAndDefaultFlags_whenRendered_thenClearAndEyeIconsAreBothShown() {
        var passwordValue by mutableStateOf("password")

        composeRule.setContent {
            DayoTheme {
                DayoPasswordTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it }
                )
            }
        }

        assertContentDescriptionCount("Clear password", 1)
        assertContentDescriptionCount("Show password", 1)
    }

    @Test
    fun givenText_whenClearIconTapped_thenFieldIsEmptied() {
        var passwordValue by mutableStateOf("password")

        composeRule.setContent {
            DayoTheme {
                DayoPasswordTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Clear password").performClick()

        composeRule.runOnIdle {
            assertEquals("", passwordValue)
        }
        assertContentDescriptionCount("Clear password", 0)
        assertContentDescriptionCount("Show password", 1)
    }

    @Test
    fun givenDefaultVisibilityIcon_whenTapped_thenContentDescriptionChangesToHidePassword() {
        var passwordValue by mutableStateOf("password")

        composeRule.setContent {
            DayoTheme {
                DayoPasswordTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Show password").performClick()

        assertContentDescriptionCount("Hide password", 1)
    }

    @Test
    fun givenErrorState_whenRendered_thenOnlyErrorIconIsShown() {
        var passwordValue by mutableStateOf("password")

        composeRule.setContent {
            DayoTheme {
                DayoPasswordTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it },
                    isError = true,
                    errorMessage = "error"
                )
            }
        }

        assertContentDescriptionCount("error icon", 1)
        assertContentDescriptionCount("Clear password", 0)
        assertContentDescriptionCount("Show password", 0)
    }

    @Test
    fun givenVisibilityIconHiddenAndTextExists_whenRendered_thenOnlyClearIconIsShown() {
        var passwordValue by mutableStateOf("password")

        composeRule.setContent {
            DayoTheme {
                DayoPasswordTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it },
                    showVisibilityIcon = false
                )
            }
        }

        assertContentDescriptionCount("Clear password", 1)
        assertContentDescriptionCount("Show password", 0)
        assertContentDescriptionCount("Hide password", 0)
    }

    @Test
    fun givenVisibilityIconHiddenAndTextBlank_whenRendered_thenNoTrailingIconsAreShown() {
        var passwordValue by mutableStateOf("")

        composeRule.setContent {
            DayoTheme {
                DayoPasswordTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it },
                    showVisibilityIcon = false
                )
            }
        }

        assertContentDescriptionCount("Clear password", 0)
        assertContentDescriptionCount("Show password", 0)
        assertContentDescriptionCount("Hide password", 0)
        assertContentDescriptionCount("error icon", 0)
    }

    @Test
    fun givenDisabledFieldWithText_whenRendered_thenClearIsHiddenAndEyeRemains() {
        var passwordValue by mutableStateOf("password")

        composeRule.setContent {
            DayoTheme {
                DayoPasswordTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it },
                    isEnabled = false
                )
            }
        }

        assertContentDescriptionCount("Clear password", 0)
        assertContentDescriptionCount("Show password", 1)
    }
}
