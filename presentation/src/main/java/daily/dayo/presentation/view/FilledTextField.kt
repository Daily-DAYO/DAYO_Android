package daily.dayo.presentation.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.Red_FF4545
import kotlinx.coroutines.delay

@Composable
fun FilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    textAlign: TextAlign = TextAlign.Left,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = if (label != "") {
            { Text(text = label) }
        } else null,
        placeholder = { Text(text = placeholder) },
        modifier = modifier,
        isError = isError,
        supportingText = { if (isError) Text(text = errorMessage) else Text(text = "") },
        textStyle = TextStyle(textAlign = textAlign, color = Gray1_313131),
        colors = TextFieldDefaults.colors(
            errorSupportingTextColor = Red_FF4545, // 에러 메시지
            focusedIndicatorColor = PrimaryGreen_23C882, // 밑줄
            unfocusedIndicatorColor = Gray6_F0F1F3,
            errorIndicatorColor = Red_FF4545,
            unfocusedContainerColor = Color.Transparent, // 배경
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedLabelColor = Color.Transparent, // 라벨
            focusedLabelColor = Gray4_C5CAD2,
            errorLabelColor = Red_FF4545,
            focusedPlaceholderColor = Gray5_E8EAEE, // 힌트
            unfocusedPlaceholderColor = Gray5_E8EAEE,
            disabledPlaceholderColor = Gray5_E8EAEE
        ),
        trailingIcon = if (isError) {
            { Icon(imageVector = Icons.Filled.Cancel, contentDescription = "error", tint = Red_FF4545) }
        } else trailingIcon
    )
}

@Composable
fun FilledPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textAlign: TextAlign = TextAlign.Left,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = if (label != "") {
            { Text(text = label) }
        } else null,
        placeholder = { Text(text = placeholder) },
        modifier = modifier,
        isError = isError,
        supportingText = { if (isError) Text(text = errorMessage) else Text(text = "") },
        textStyle = TextStyle(textAlign = textAlign, color = Gray1_313131),
        colors = TextFieldDefaults.colors(
            errorSupportingTextColor = Red_FF4545,
            focusedIndicatorColor = PrimaryGreen_23C882,
            unfocusedIndicatorColor = Gray6_F0F1F3,
            errorIndicatorColor = Red_FF4545,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedLabelColor = Color.Transparent,
            focusedLabelColor = Gray4_C5CAD2,
            errorLabelColor = Red_FF4545,
            focusedPlaceholderColor = Gray5_E8EAEE,
            unfocusedPlaceholderColor = Gray5_E8EAEE,
            disabledPlaceholderColor = Gray5_E8EAEE
        ),
        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            if (isError) {
                Icon(imageVector = Icons.Filled.Cancel, contentDescription = "error", tint = Red_FF4545)
            } else {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val iconColor = if (passwordHidden) Gray5_E8EAEE else PrimaryGreen_23C882
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(imageVector = Icons.Filled.Visibility, contentDescription = description, tint = iconColor)
                }
            }
        }
    )
}

@Composable
fun FilledTimerField(
    value: String,
    onValueChange: (String) -> Unit,
    seconds: Int,
    isPaused: Boolean,
    label: String = "",
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textAlign: TextAlign = TextAlign.Left,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    var timeLeft by rememberSaveable { mutableStateOf(seconds) }
    LaunchedEffect(key1 = timeLeft, key2 = isPaused) {
        while (timeLeft > 0 && !isPaused) {
            delay(1000L)
            timeLeft--
        }
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = if (label != "") {
            { Text(text = label) }
        } else null,
        placeholder = { Text(text = placeholder) },
        modifier = modifier,
        isError = isError,
        supportingText = { if (isError) Text(text = errorMessage) else Text(text = "") },
        textStyle = TextStyle(textAlign = textAlign, color = Gray1_313131),
        trailingIcon = { Text(text = "${String.format("%02d", (timeLeft / 60) % 60)}:${String.format("%02d", timeLeft % 60)}", color = Gray2_767B83) },
        colors = TextFieldDefaults.colors(
            errorSupportingTextColor = Red_FF4545,
            focusedIndicatorColor = PrimaryGreen_23C882,
            unfocusedIndicatorColor = Gray6_F0F1F3,
            errorIndicatorColor = Red_FF4545,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedLabelColor = Color.Transparent,
            focusedLabelColor = Gray4_C5CAD2,
            errorLabelColor = Red_FF4545,
            focusedPlaceholderColor = Gray5_E8EAEE,
            unfocusedPlaceholderColor = Gray5_E8EAEE,
            disabledPlaceholderColor = Gray5_E8EAEE
        )
    )
}

@Preview
@Composable
fun PreviewTextField() {
    Column {
        // Default 사용 예시
        val text = rememberSaveable { mutableStateOf("") }
        val isError = text.value == "error"
        FilledTextField(
            value = text.value,
            onValueChange = { textValue -> text.value = textValue },
            label = "Text",
            isError = isError,
            errorMessage = "error",
            placeholder = "text를 입력하세요"
        )

        // Password 사용 예시
        val password = rememberSaveable { mutableStateOf("") }
        FilledPasswordField(
            value = password.value,
            onValueChange = { textValue -> password.value = textValue },
            placeholder = "비밀번호를 입력하세요"
        )

        // Timer 사용 예시
        val timerText = rememberSaveable { mutableStateOf("") }
        var isPaused by rememberSaveable { mutableStateOf(false) }
        FilledTimerField(
            value = timerText.value,
            onValueChange = { textValue -> timerText.value = textValue },
            seconds = 60,
            isPaused = isPaused,
            label = "Number",
            isError = isError,
            errorMessage = "error",
        )
    }
}