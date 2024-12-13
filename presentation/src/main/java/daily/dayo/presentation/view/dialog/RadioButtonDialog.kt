package daily.dayo.presentation.view.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.CharacterLimitOutlinedTextField
import daily.dayo.presentation.view.DayoTextButton
import kotlinx.coroutines.launch

@Composable
fun RadioButtonDialog(
    title: String,
    description: String,
    radioItems: ArrayList<String>,
    onClickCancel: () -> Unit,
    onClickConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    lastInputEnabled: Boolean = false, // 마지막 아이템 선택 시 edit text 입력창 여부
    lastTextPlaceholder: String = "",
    lastTextMaxLength: Int = 100
) {
    val selectedIndex = remember { mutableStateOf<Int?>(null) }
    val lastTextValue = remember { mutableStateOf(TextFieldValue("")) }

    Dialog(
        onDismissRequest = onClickCancel
    ) {
        Surface(modifier = modifier.padding(vertical = 24.dp)) {
            Column {
                DialogHeader(title, description)

                Box(modifier = Modifier.weight(1f)) {
                    DialogRadioButtons(
                        radioItems,
                        selectedIndex,
                        lastInputEnabled,
                        lastTextValue,
                        lastTextPlaceholder,
                        lastTextMaxLength
                    )
                }

                DialogActionButton(
                    onClickCancel,
                    selectedIndex,
                    radioItems,
                    lastInputEnabled,
                    lastTextValue,
                    onClickConfirm
                )
            }
        }
    }
}

@Composable
private fun DialogHeader(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = title,
            style = DayoTheme.typography.b1.copy(
                color = Dark,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = description,
            style = DayoTheme.typography.caption1.copy(
                color = Gray3_9FA5AE
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}

@Composable
private fun DialogRadioButtons(
    radioItems: ArrayList<String>,
    selectedIndex: MutableState<Int?>,
    lastInputEnabled: Boolean,
    lastTextValue: MutableState<TextFieldValue>,
    lastTextPlaceholder: String,
    lastTextMaxLength: Int
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val isTextFieldVisible = remember { mutableStateOf(false) }

    LazyColumn(state = scrollState) {
        radioItems.forEachIndexed { index, text ->
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedIndex.value != null && selectedIndex.value == index,
                            onClick = {
                                selectedIndex.value = index
                                if (lastInputEnabled && selectedIndex.value == radioItems.lastIndex) {
                                    isTextFieldVisible.value = true
                                    coroutineScope.launch {
                                        scrollState.animateScrollToItem(index + 1)
                                    }
                                } else {
                                    isTextFieldVisible.value = false
                                    focusManager.clearFocus()
                                }
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedIndex.value != null && selectedIndex.value == index,
                        onClick = {
                            selectedIndex.value = index
                            if (lastInputEnabled && selectedIndex.value == radioItems.lastIndex) {
                                isTextFieldVisible.value = true
                                coroutineScope.launch {
                                    scrollState.animateScrollToItem(index + 1)
                                }
                            } else {
                                isTextFieldVisible.value = false
                                focusManager.clearFocus()
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Primary_23C882,
                            unselectedColor = Gray3_9FA5AE
                        )
                    )
                    Text(
                        text = text,
                        style = DayoTheme.typography.b4.copy(color = Color(0xFF50545B)),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        item {
            if (isTextFieldVisible.value) {
                SideEffect {
                    focusRequester.requestFocus()
                }

                CharacterLimitOutlinedTextField(
                    value = lastTextValue,
                    placeholder = lastTextPlaceholder,
                    maxLength = lastTextMaxLength,
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .height(144.dp)
                        .focusRequester(focusRequester)
                )
            }
        }
    }
}

@Composable
private fun DialogActionButton(
    onClickCancel: () -> Unit,
    selectedIndex: MutableState<Int?>,
    radioItems: ArrayList<String>,
    lastInputEnabled: Boolean,
    lastTextValue: MutableState<TextFieldValue>,
    onClickConfirm: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        DayoTextButton(
            onClick = onClickCancel,
            text = stringResource(id = R.string.cancel),
            textStyle = DayoTheme.typography.b6.copy(
                color = Gray2_767B83
            ),
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
        )

        val canSubmit = selectedIndex.value != null && when {
            !lastInputEnabled -> true // 기타 사유를 입력하지 않아도 되는 경우
            selectedIndex.value == radioItems.lastIndex -> lastTextValue.value.text.isNotBlank() // 기타 사유 필수 입력
            else -> true // 기타 항목을 선택하지 않은 경우 
        }

        DayoTextButton(
            onClick = {
                selectedIndex.value?.let { index ->
                    val item = if (index == radioItems.lastIndex && lastInputEnabled) {
                        lastTextValue.value.text.ifBlank { return@let }
                    } else {
                        radioItems[index]
                    }
                    onClickConfirm(item)
                }
            },
            text = stringResource(id = R.string.submit),
            textStyle = DayoTheme.typography.b6.copy(
                color = if (canSubmit) Primary_23C882 else Gray4_C5CAD2
            ),
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewRadioButtonDialog() {
    var showDialog by remember { mutableStateOf(false) }
    val reportReasons = arrayListOf(
        stringResource(id = R.string.report_post_reason_1),
        stringResource(id = R.string.report_post_reason_2),
        stringResource(id = R.string.report_post_reason_3),
        stringResource(id = R.string.report_post_reason_4),
        stringResource(id = R.string.report_post_reason_5),
        stringResource(id = R.string.report_post_reason_6),
        stringResource(id = R.string.report_post_reason_7),
        stringResource(id = R.string.report_post_reason_other),
    )

    DayoTheme {
        RadioButtonDialog(
            title = stringResource(id = R.string.report_post_title),
            description = stringResource(id = R.string.report_post_description),
            radioItems = reportReasons,
            lastInputEnabled = true,
            lastTextPlaceholder = "게시물을 신고하는 기타 사유는 무엇인가요?",
            lastTextMaxLength = 100,
            onClickCancel = { showDialog = !showDialog },
            onClickConfirm = {},
            modifier = Modifier
                .height(400.dp)
                .imePadding()
                .clip(RoundedCornerShape(28.dp))
                .background(White_FFFFFF)
        )
    }
}