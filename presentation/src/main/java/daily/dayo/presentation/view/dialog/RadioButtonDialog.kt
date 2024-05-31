package daily.dayo.presentation.view.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.b1
import daily.dayo.presentation.theme.b4
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption1

@Composable
fun RadioButtonDialog(
    radioItems: ArrayList<String>,
    lastInputEnabled: Boolean = false, // 마지막 아이템 선택 시 edit text 입력창 여부
    onClickCancel: () -> Unit,
    onClickConfirm: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val selectedIndex = remember { mutableStateOf<Int?>(null) }

    Dialog(
        onDismissRequest = onClickCancel,
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
                        text = stringResource(id = R.string.report_post_title),
                        style = MaterialTheme.typography.b1.copy(
                            color = Gray1_313131,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.report_post_description),
                        style = MaterialTheme.typography.caption1.copy(
                            color = Gray3_9FA5AE
                        ),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                BoxWithConstraints(modifier = Modifier.weight(1f, fill = true)) {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        radioItems.forEachIndexed { index, text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedIndex.value != null && selectedIndex.value == index,
                                        onClick = { selectedIndex.value = index }
                                    )
                                    .padding(start = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedIndex.value != null && selectedIndex.value == index,
                                    onClick = { selectedIndex.value = index }
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.b4.copy(color = Color(0xFF50545B)),
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        }

                        if (lastInputEnabled && selectedIndex.value == radioItems.lastIndex) {
                            // todo
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
                        textStyle = MaterialTheme.typography.b6.copy(
                            color = Gray2_767B83
                        ),
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                    )

                    daily.dayo.presentation.view.TextButton(
                        onClick = {
                            if (selectedIndex.value != null) {
                                if (lastInputEnabled && selectedIndex.value == radioItems.lastIndex) {
                                    onClickConfirm("input text") // todo
                                } else {
                                    onClickConfirm(radioItems[selectedIndex.value!!])
                                }
                            }
                        },
                        text = stringResource(id = R.string.submit),
                        textStyle = MaterialTheme.typography.b6.copy(
                            color = if (selectedIndex.value != null) PrimaryGreen_23C882 else Gray4_C5CAD2
                        ),
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                    )
                }
            }
        }
    }
}