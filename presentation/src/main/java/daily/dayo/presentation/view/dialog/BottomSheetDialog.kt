package daily.dayo.presentation.view.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b1
import daily.dayo.presentation.theme.b4
import kotlinx.coroutines.launch

// 1. 기본과 Hover 상태 구분
// 2. primary가 설정되는 경우 가장 1번째 색이 항상 prmiary color로 설정, 아닌 경우 모두 같은 색
// 3. 가운데 정렬과 좌측 정렬 설정
// 4. 좌측 정렬하는 경우 좌측과 우측에 아이콘 존재 가능.

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(
    sheetState: ModalBottomSheetState,
    buttons: List<Pair<String, () -> Unit>>,
    leftIconButtons: List<ImageVector>? = null,
    leftIconCheckedButtons: List<ImageVector>? = null,
    isFirstButtonColored: Boolean = false,
    normalColor: Color = Dark,
    checkedColor: Color = Primary_23C882,
    title: String = "",
    titleButtonAction: () -> Unit = {},
    rightIcon: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_check_mark),
    checkedButtonIndex: Int = -1,
    closeButtonAction: (() -> Unit)? = null
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
        sheetContent = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    if (title.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(White_FFFFFF)
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(12.dp),
                                textAlign = TextAlign.Center,
                                color = Dark,
                                style = MaterialTheme.typography.b1
                            )
                            androidx.compose.material3.IconButton(
                                onClick = titleButtonAction,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .wrapContentSize()
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_x_sign),
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .wrapContentSize()
                                        .align(Alignment.CenterEnd)
                                        .clickable(onClick = closeButtonAction ?: { }),
                                    contentDescription = "x sign",
                                )
                            }
                        }
                    }

                    buttons.forEachIndexed { index, button ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed = interactionSource.collectIsPressedAsState().value

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    if (isPressed) Gray6_F0F1F3 else White_FFFFFF,
                                    RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)
                                )
                                .padding(if (leftIconButtons == null) 16.dp else 12.dp)
                                .clickable(
                                    onClick = button.second,
                                    interactionSource = interactionSource,
                                    indication = null
                                ),
                            horizontalArrangement = if (leftIconButtons == null) Arrangement.Center else Arrangement.SpaceBetween,
                        ) {
                            if (leftIconButtons != null && leftIconCheckedButtons != null) {
                                Icon(
                                    imageVector = if (checkedButtonIndex == index) leftIconCheckedButtons[index] else leftIconButtons[index],
                                    contentDescription = "",
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    tint = Color.Unspecified
                                )
                            }
                            Text(
                                text = button.first,
                                modifier = Modifier.offset(
                                    if (leftIconButtons == null) 0.dp else 8.dp,
                                    0.dp
                                ),
                                color = if ((isFirstButtonColored && index == 0) || (checkedButtonIndex == index)) checkedColor else normalColor,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.b4
                            )
                            if (leftIconButtons != null) {
                                Spacer(modifier = Modifier.weight(1f))
                                if (checkedButtonIndex == index) {
                                    Icon(
                                        imageVector = rightIcon,
                                        contentDescription = "",
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        tint = Color.Unspecified
                                    )
                                }
                            }
                        }
                        if (index < buttons.size - 1 && title.isEmpty()) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(0.dp),
                                color = Gray6_F0F1F3
                            )
                        }
                    }
                }
            }
        }
    ) { }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun getBottomSheetDialogState(
    disableFullExpanded: Boolean = false,
    skipHalfExpanded: Boolean = true
): ModalBottomSheetState {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            if (disableFullExpanded)
                it != ModalBottomSheetValue.Expanded
            else
                true
        },
        skipHalfExpanded = skipHalfExpanded
    )

    return bottomSheetState
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PreviewMyBottomSheetDialog() {

    // BottomSheetDialog를 사용하는 경우 Box를 이용해서 겹쳐보일 수 있도록 설정해야 합니다
    Box(modifier = Modifier.fillMaxSize()) {
        val coroutineScope = rememberCoroutineScope()

        // BottomSheetDialog를 사용하는 경우에 같이 사용하는 State
        val bottomSheetState1 = getBottomSheetDialogState()
        val bottomSheetState2 = getBottomSheetDialogState()
        val bottomSheetState3 = getBottomSheetDialogState()
        val bottomSheetState4 = getBottomSheetDialogState()
        val bottomSheetState5 = getBottomSheetDialogState()
        val bottomSheetState6 = getBottomSheetDialogState()
        val bottomSheetState7 = getBottomSheetDialogState()

        Column(modifier = Modifier.fillMaxSize()) {
            // 구현 본문 내용

            // BottomSheetDialog를 보여주는 경우 클릭하는 Button
            Button(onClick = { coroutineScope.launch { bottomSheetState1.show() } }) {
                Text(text = "Bottom Sheet Dialog / buton 1 / non-primary")
            }
            Button(onClick = { coroutineScope.launch { bottomSheetState2.show() } }) {
                Text(text = "Bottom Sheet Dialog / buton 1 / primary")
            }
            Button(onClick = { coroutineScope.launch { bottomSheetState3.show() } }) {
                Text(text = "Bottom Sheet Dialog / buton 2 / non-primary")
            }
            Button(onClick = { coroutineScope.launch { bottomSheetState4.show() } }) {
                Text(text = "Bottom Sheet Dialog / buton 2 / primary")
            }
            Button(onClick = { coroutineScope.launch { bottomSheetState5.show() } }) {
                Text(text = "Bottom Sheet Dialog / buton 3 / non-primary")
            }
            Button(onClick = { coroutineScope.launch { bottomSheetState6.show() } }) {
                Text(text = "Bottom Sheet Dialog / buton 3 / primary")
            }
            Button(onClick = { coroutineScope.launch { bottomSheetState7.show() } }) {
                Text(text = "Bottom Sheet Dialog / Category")
            }
        }

        BottomSheetDialog(
            sheetState = bottomSheetState1,
            buttons = listOf(
                Pair("text") {
                    // 버튼 클릭시 동작 의
                }),
            isFirstButtonColored = false,
        )
        BottomSheetDialog(
            sheetState = bottomSheetState2,
            buttons = listOf(Pair("text") { }),
            isFirstButtonColored = true
        )

        BottomSheetDialog(
            sheetState = bottomSheetState3,
            buttons = listOf(Pair("text") { }, Pair("text") { }),
            isFirstButtonColored = false,
        )
        BottomSheetDialog(
            sheetState = bottomSheetState4,
            buttons = listOf(Pair("text") { }, Pair("text") { }),
            isFirstButtonColored = true
        )

        BottomSheetDialog(
            sheetState = bottomSheetState5,
            buttons = listOf(Pair("text") { }, Pair("text") { }, Pair("text") { }),
            isFirstButtonColored = false,
        )
        BottomSheetDialog(
            sheetState = bottomSheetState6,
            buttons = listOf(Pair("text") { }, Pair("text") { }, Pair("text") { }),
            isFirstButtonColored = true
        )

        BottomSheetDialog(
            sheetState = bottomSheetState7,
            buttons = listOf(Pair("contents") { }, Pair("contents") { }, Pair("contents") { }),
            title = "title",
            leftIconButtons = listOf(Icons.Default.Image, Icons.Default.Image, Icons.Default.Image),
            leftIconCheckedButtons = listOf(Icons.Default.ImageSearch, Icons.Default.ImageSearch, Icons.Default.ImageSearch),
            checkedButtonIndex = 0,
        )
    }
}