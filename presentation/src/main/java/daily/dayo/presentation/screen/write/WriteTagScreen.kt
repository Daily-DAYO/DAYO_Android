package daily.dayo.presentation.screen.write

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.limitTo
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.PrimaryL1_8FD9B9
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.WriteViewModel

const val TAG_MAX_COUNT = 8
const val TAG_MAX_LENGTH = 20

@Composable
fun WriteTagRoute(
    onBackClick: () -> Unit,
    writeViewModel: WriteViewModel = hiltViewModel(),
) {
    val currentTags = remember { mutableStateOf(writeViewModel.writeTags.value) }
    BackHandler {
        onBackClick()
    }

    WriteTagScreen(
        onBackClick = onBackClick,
        currentTags = currentTags.value,
        onAddTagsClick = {
            // 8글자 제한 한번 더 확인
            currentTags.value =
                currentTags.value.toMutableList().apply { add(it.limitTo(TAG_MAX_LENGTH)) }
        },
        onRemoveTagClick = {
            currentTags.value = currentTags.value.toMutableList().apply { remove(it) }
        },
        onSaveClick = {
            writeViewModel.updatePostTags(currentTags.value.limitTo(TAG_MAX_COUNT))
            onBackClick()
        }
    )
}

@Composable
fun WriteTagScreen(
    onBackClick: () -> Unit,
    onRemoveTagClick: (String) -> Unit,
    onAddTagsClick: (String) -> Unit = {},
    onSaveClick: () -> Unit,
    currentTags: List<String>,
) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true),
            ) {
                WritTagActionBarLayout(
                    onBackClick = onBackClick,
                    onSaveClick = onSaveClick
                )
                WriteTagContent(
                    currentTags = currentTags,
                    onRemoveTagClick = onRemoveTagClick
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                WriteTadAdd(
                    currentTags = currentTags,
                    currentTagCount = currentTags.size,
                    onAddTagsClick = onAddTagsClick
                )
            }
        }
    }
}

@Composable
fun WritTagActionBarLayout(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column {
        TopNavigation(
            leftIcon = {
                NoRippleIconButton(
                    onClick = onBackClick,
                    iconContentDescription = "Previous Page",
                    iconPainter = painterResource(id = R.drawable.ic_arrow_left)
                )
            },
            title = stringResource(R.string.write_post_tag_title),
            rightIcon = {
                DayoTextButton(
                    onClick = {
                        onSaveClick()
                    },
                    text = stringResource(id = R.string.save),
                    textStyle = DayoTheme.typography.b3.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(10.dp),
                )
            },
            titleAlignment = TopNavigationAlign.CENTER
        )
    }
}

@Composable
fun WriteTagContent(
    currentTags: List<String>,
    onRemoveTagClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            WriteTags(
                tags = currentTags,
                onRemoveTagClick = { onRemoveTagClick(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WriteTags(
    tags: List<String> = listOf(),
    onRemoveTagClick: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(tags.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tagText ->
                InputChip(
                    modifier = Modifier
                        .defaultMinSize(1.dp, 1.dp)
                        .background(
                            color = PrimaryL3_F2FBF7,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .height(36.dp),
                    border = null,
                    selected = false,
                    onClick = { /* 클릭 동작 없음 */ },
                    label = {
                        Text(
                            text = tagText,
                            style = DayoTheme.typography.b5.copy(
                                color = Primary_23C882
                            )
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp, end = 0.dp)
                                .background(
                                    color = PrimaryL1_8FD9B9,
                                    shape = CircleShape
                                )
                                .padding(
                                    vertical = 5.dp,
                                    horizontal = 4.dp
                                )
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_tag),
                                contentDescription = tagText,
                                tint = Primary_23C882
                            )
                        }
                    },
                    trailingIcon = {
                        Box(
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onRemoveTagClick(tagText) }
                                    .padding(4.dp),
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_x),
                                contentDescription = "Remove $tagText",
                                tint = Gray4_C5CAD2,
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WriteTadAdd(
    currentTags: List<String> = listOf(),
    currentTagCount: Int = 0,
    maxTagCount: Int = TAG_MAX_COUNT,
    onAddTagsClick: (String) -> Unit = {}
) {
    var writeContentValue by remember { mutableStateOf(TextFieldValue()) }
    // 추가조건
    // 1. 태그 내용이 비어있지 않아야 한다. (writeContentValue.text.isNotEmpty())
    // 2. 현재 태그 개수가 최대 태그 개수보다 작아야 한다. (currentTagCount < maxTagCount)
    // 3. 태그 내용이 20자 이하여야 한다. (writeContentValue.text.length <= 20)
    // 4. 현재 태그 목록에 같은 태그가 없어야 한다. (!currentTags.contains(writeContentValue.text))
    val addBtnEnabled =
        writeContentValue.text.isNotEmpty()
                && currentTagCount < maxTagCount
                && writeContentValue.text.length <= 20
                && !currentTags.contains(writeContentValue.text)
    Column(
        modifier = Modifier
            .background(color = White_FFFFFF)
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 12.dp,
                bottom = 20.dp
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
    ) {
        Row(

        ) {
            Text(
                modifier = Modifier.padding(
                    vertical = 2.dp
                ),
                text = stringResource(R.string.write_post_tag_subheading),
                style = DayoTheme.typography.b6.copy(
                    color = Gray1_50545B
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = PrimaryL3_F2FBF7,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        vertical = 2.dp,
                        horizontal = 8.dp,
                    )
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = DayoTheme.typography.caption2.toSpanStyle().copy(
                                color = Primary_23C882
                            )
                        ) {
                            append("$currentTagCount ")
                        }
                        withStyle(
                            style = DayoTheme.typography.caption2.toSpanStyle()
                                .copy(color = Gray4_C5CAD2)
                        ) {
                            append(
                                stringResource(R.string.write_post_tag_count_limit).format(
                                    maxTagCount
                                )
                            )
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = writeContentValue,
                onValueChange = {
                    if (it.text.length > 20) return@BasicTextField
                    writeContentValue = it
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Gray7_F6F6F7,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp,
                    )
                    .align(alignment = Alignment.CenterVertically),
                textStyle = DayoTheme.typography.b6.copy(
                    color = Dark,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                decorationBox = { innerTextField ->
                    if (writeContentValue.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.write_post_tag_input_placeholder),
                            style = DayoTheme.typography.b6.copy(
                                color = Gray4_C5CAD2,
                            )
                        )
                    }
                    innerTextField()
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            DayoTextButton(
                modifier = Modifier
                    .background(
                        color = if (addBtnEnabled) {
                            Primary_23C882
                        } else {
                            PrimaryL1_8FD9B9
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
                    .align(alignment = Alignment.CenterVertically),
                onClick = {
                    if (!addBtnEnabled) {
                        return@DayoTextButton
                    } else {
                        onAddTagsClick(writeContentValue.text)
                        writeContentValue = TextFieldValue()
                    }
                },
                text = stringResource(R.string.add),
                textStyle = DayoTheme.typography.b5.copy(
                    color = if (addBtnEnabled) {
                        PrimaryL3_F2FBF7
                    } else {
                        White_FFFFFF
                    },
                ),
            )
        }
    }
}

@Preview
@Composable
fun WriteTagAddPreview() {
    WriteTadAdd()
}