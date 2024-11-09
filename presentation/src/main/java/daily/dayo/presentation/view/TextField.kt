package daily.dayo.presentation.view

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.common.TextLimitUtil
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.Red_FF4545
import daily.dayo.presentation.theme.b4
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption3
import daily.dayo.presentation.theme.caption4
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 0.dp, bottom = 8.dp),
    label: String = "",
    placeholder: String = "",
    @DrawableRes trailingIconId: Int? = null,
    isError: Boolean? = null,
    errorMessage: String = "",
    textAlign: TextAlign = TextAlign.Left,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onTrailingIconClick: (() -> Unit) = { }
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.caption3.copy(
                    color = Gray4_C5CAD2,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.align(alignment = Alignment.CenterEnd)
            ) {
                Column {
                    if (isError != null && isError == true) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "error",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(onClick = onTrailingIconClick),
                            tint = Red_FF4545
                        )
                    } else if (trailingIconId != null) {
                        Icon(
                            painter = painterResource(id = trailingIconId),
                            contentDescription = "error",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(onClick = onTrailingIconClick),
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(textAlign = textAlign, color = Dark),
                interactionSource = interactionSource,
                decorationBox = @Composable { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value,
                        visualTransformation = VisualTransformation.None,
                        innerTextField = innerTextField,
                        placeholder = {
                            Text(
                                text = placeholder,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.b4.copy(Gray4_C5CAD2)
                            )
                        },
                        singleLine = true,
                        isError = isError ?: false,
                        enabled = true,
                        interactionSource = interactionSource,
                        contentPadding = contentPadding,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Primary_23C882, // 밑줄
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
                        )
                    )
                }
            )
        }

        if (isError != null) {
            Text(
                text = if (isError) errorMessage else "",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.caption4.copy(Red_FF4545)
            )
        }
    }
}

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
        textStyle = TextStyle(textAlign = textAlign, color = Dark),
        colors = TextFieldDefaults.colors(
            errorSupportingTextColor = Red_FF4545, // 에러 메시지
            focusedIndicatorColor = Primary_23C882, // 밑줄
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
        textStyle = TextStyle(textAlign = textAlign, color = Dark),
        colors = TextFieldDefaults.colors(
            errorSupportingTextColor = Red_FF4545,
            focusedIndicatorColor = Primary_23C882,
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
                    val iconColor = if (passwordHidden) Gray5_E8EAEE else Primary_23C882
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
        textStyle = TextStyle(textAlign = textAlign, color = Dark),
        trailingIcon = { Text(text = "${String.format("%02d", (timeLeft / 60) % 60)}:${String.format("%02d", timeLeft % 60)}", color = Gray2_767B83) },
        colors = TextFieldDefaults.colors(
            errorSupportingTextColor = Red_FF4545,
            focusedIndicatorColor =
            Primary_23C882,
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

@Composable
fun CharacterLimitOutlinedTextField(
    value: MutableState<TextFieldValue>,
    maxLength: Int,
    singleLine: Boolean = false,
    cornerSize: Dp = 8.dp,
    placeholder: String = "",
    outlinedTextFieldColors: TextFieldColors? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value.value.text,
        onValueChange = { textValue ->
            value.value = value.value.copy(
                text = TextLimitUtil.trimToMaxLength(textValue, maxLength)
            )
        },
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.b6) },
        singleLine = singleLine,
        shape = RoundedCornerShape(cornerSize),
        colors = outlinedTextFieldColors
            ?: OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Gray7_F6F6F7,
                unfocusedContainerColor = Gray7_F6F6F7,
                focusedBorderColor = Primary_23C882,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Primary_23C882
            ),
        textStyle = MaterialTheme.typography.b6,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewTextField() {
    Column(Modifier.fillMaxWidth()) {
        // Default 사용 예시
        val text = rememberSaveable { mutableStateOf("") }
        val isError = text.value == "error"

        DayoTextField(
            value = text.value,
            onValueChange = { textValue -> text.value = textValue },
            modifier = Modifier.padding(horizontal = 16.dp),
            label = "label",
            trailingIconId = R.drawable.ic_img_delete_gray_5,
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
        val isPaused by rememberSaveable { mutableStateOf(false) }
        FilledTimerField(
            value = timerText.value,
            onValueChange = { textValue -> timerText.value = textValue },
            seconds = 60,
            isPaused = isPaused,
            label = "Number",
            isError = isError,
            errorMessage = "error",
        )

        // 글자수 제한 text field 사용 예시
        val limitText = remember { mutableStateOf(TextFieldValue("")) }
        CharacterLimitOutlinedTextField(
            value = limitText,
            placeholder = "게시물을 신고하는 기타 사유는 무엇인가요?",
            maxLength = 5
        )
    }
}