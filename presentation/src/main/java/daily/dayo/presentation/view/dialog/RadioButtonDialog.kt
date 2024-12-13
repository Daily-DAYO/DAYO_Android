package daily.dayo.presentation.view.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.CharacterLimitOutlinedTextField
import kotlinx.coroutines.launch

@Composable
fun RadioButtonDialog(
    title: String,
    description: String,
    radioItems: ArrayList<String>,
    lastInputEnabled: Boolean = false, // 마지막 아이템 선택 시 edit text 입력창 여부
    lastTextPlaceholder: String = "",
    lastTextMaxLength: Int = 100,
    onClickCancel: () -> Unit,
    onClickConfirm: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val selectedIndex = remember { mutableStateOf<Int?>(null) }
    val lastTextValue = remember { mutableStateOf(TextFieldValue("")) }
    val isTextFieldVisible = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = onClickCancel
    ) {
        Surface(modifier = modifier) {
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier
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

                BoxWithConstraints(modifier = Modifier.weight(1f)) {
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    daily.dayo.presentation.view.TextButton(
                        onClick = onClickCancel,
                        text = stringResource(id = R.string.cancel),
                        textStyle = DayoTheme.typography.b6.copy(
                            color = Gray2_767B83
                        ),
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                    )

                    val canSubmit = selectedIndex.value != null && (
                            if (selectedIndex.value == radioItems.lastIndex) lastInputEnabled && lastTextValue.value.text.isNotBlank() || !lastInputEnabled
                            else true
                            )

                    daily.dayo.presentation.view.TextButton(
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
        }
    }
}