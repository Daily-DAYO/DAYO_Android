package daily.dayo.presentation.screen.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.domain.model.SearchHistory
import daily.dayo.domain.model.SearchHistoryDetail
import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.presentation.R
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.common.toSp
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SearchRoute(
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit,
    viewmodel: SearchViewModel = hiltViewModel()
) {
    BackHandler {
        onBackClick()
    }

    val searchHistory by viewmodel.searchHistory.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewmodel.getSearchKeywordRecent()
    }

    searchHistory?.let {
        SearchScreen(
            searchHistory = it,
            onBackClick = onBackClick,
            onSearchClick = { keyword ->
                onSearch(keyword)
            },
            onKeywordDeleteClick = { keyword, type ->
                coroutineScope.launch {
                    viewmodel.deleteSearchKeywordRecent(
                        keyword,
                        type
                    )
                }
            },
            onHistoryClearClick = {
                coroutineScope.launch {
                    viewmodel.clearSearchKeywordRecent()
                }
            }
        )
    }
}

@Composable
fun SearchScreen(
    searchHistory: SearchHistory,
    onBackClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onKeywordDeleteClick: (String, SearchHistoryType) -> Unit,
    onHistoryClearClick: () -> Unit
) {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column {
            SearchActionbarLayout(
                onBackClick = onBackClick,
                onSearchClick = onSearchClick
            )
            SetSearchHistoryLayout(
                onKeywordClick = onSearchClick,
                onKeywordDeleteClick = onKeywordDeleteClick,
                onHistoryClearClick = onHistoryClearClick,
                searchHistory = searchHistory
            )
        }
    }
}

@Composable
fun SearchActionbarLayout(
    initialKeyword: String = "",
    onBackClick: () -> Unit,
    onSearchClick: (String) -> Unit
) {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        var textFieldValue by remember { mutableStateOf(TextFieldValue(initialKeyword)) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 6.dp, end = 14.dp, top = 2.dp, bottom = 2.dp),
        ) {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = "Previous Page",
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1F)
                    .padding(vertical = 4.dp)
                    .background(
                        colorResource(id = R.color.gray_7_F6F6F7),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    singleLine = true,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .fillMaxWidth()
                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                    textStyle = TextStyle(
                        fontSize = 16.dp.toSp(),
                        fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                        fontWeight = FontWeight(500),
                        color = colorResource(id = R.color.gray_1_313131),
                    ),
                    decorationBox = { innerTextField ->
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.hint_search_keyword_input),
                                style = TextStyle(
                                    fontSize = 14.dp.toSp(),
                                    lineHeight = 20.dp.toSp(),
                                    fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                                    fontWeight = FontWeight(500),
                                    color = colorResource(id = R.color.gray_3_9FA5AE),
                                )
                            )
                        }
                        innerTextField()
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            val trimmedBlankText = trimBlankText(textFieldValue.text)
                            if (trimmedBlankText.isNotEmpty()) {
                                onSearchClick(trimmedBlankText)
                            }
                        }
                    )
                )
                NoRippleIconButton(
                    onClick = { textFieldValue = TextFieldValue() },
                    iconContentDescription = "Input Clear Button",
                    iconPainter = painterResource(id = R.drawable.ic_x_sign_circle_gray),
                    iconTintColor = Color.Unspecified,
                    iconButtonModifier = Modifier
                        .align(Alignment.CenterEnd)
                        .alpha(if (textFieldValue.text.isNotEmpty()) 1f else 0f),
                )
            }
        }
    }
}

@Composable
@Preview
private fun SearchHistoryGuideLayout() {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = "최근 검색",
            style = TextStyle(
                fontSize = 14.dp.toSp(),
                lineHeight = 20.dp.toSp(),
                fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                fontWeight = FontWeight(500),
                color = colorResource(id = R.color.gray_2_767B83),
            ),
            modifier = Modifier
                .padding(start = 2.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun SetClearSearchHistoryLayout(onHistoryClearClick: () -> Unit) {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp)
    ) {
        Button(
            modifier = Modifier
                .wrapContentSize()
                .background(DayoTheme.colorScheme.background)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.white_FFFFFF),
                contentColor = colorResource(id = R.color.gray_2_767B83),
            ),
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(1.dp, colorResource(id = R.color.gray_2_767B83)),
            onClick = { onHistoryClearClick() }
        ) {
            Text(
                text = stringResource(id = R.string.search_history_clear),
                style = TextStyle(
                    fontSize = 12.dp.toSp(),
                    lineHeight = 18.dp.toSp(),
                    fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                    fontWeight = FontWeight(500),
                    color = colorResource(id = R.color.gray_2_767B83),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp)
            )
        }
    }
}

@Composable
private fun SetSearchHistoryLayout(
    onKeywordClick: (String) -> Unit,
    onKeywordDeleteClick: (String, SearchHistoryType) -> Unit,
    onHistoryClearClick: () -> Unit,
    searchHistory: SearchHistory
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DayoTheme.colorScheme.background)
    ) {
        item {
            SearchHistoryGuideLayout()
        }

        if (searchHistory.data.isNotEmpty()) {
            items(searchHistory.data) {
                SearchHistoryLayout(
                    onKeywordClick = onKeywordClick,
                    onKeywordDeleteClick = onKeywordDeleteClick,
                    searchHistoryDetail = it
                )
            }
            item {
                SetClearSearchHistoryLayout(onHistoryClearClick = onHistoryClearClick)
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SearchHistoryEmpty()
                }
            }
        }
    }
}

@Composable
@Preview
fun SearchHistoryEmpty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_search_empty),
            contentDescription = "search history empty"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.search_history_empty_title),
            style = DayoTheme.typography.b3,
            color = Gray3_9FA5AE,
        )
        Text(
            modifier = Modifier.padding(vertical = 2.dp),
            text = stringResource(id = R.string.search_history_empty_description),
            style = DayoTheme.typography.caption2,
            color = Gray4_C5CAD2,
        )
    }
}

@Composable
private fun SearchHistoryLayout(
    onKeywordClick: (String) -> Unit,
    onKeywordDeleteClick: (String, SearchHistoryType) -> Unit,
    searchHistoryDetail: SearchHistoryDetail
) {
    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickableSingle {
                    onKeywordClick(searchHistoryDetail.history)
                }
                .padding(horizontal = 18.dp, vertical = 4.dp)
        ) {
            SearchHistoryDetailLayout(searchedText = searchHistoryDetail.history)
            Spacer(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .weight(1f)
            )
            SearchHistoryDeleteButton(
                onKeywordDeleteClick = onKeywordDeleteClick,
                searchHistoryDetail = searchHistoryDetail
            )
        }
    }
}

@Composable
@Preview
private fun PreviewSearchHistoryLayout() {
    SearchHistoryLayout(
        onKeywordClick = { _ -> },
        onKeywordDeleteClick = { _, _ -> },
        searchHistoryDetail = SearchHistoryDetail(
            history = "검색어",
            searchId = 0,
            searchHistoryType = SearchHistoryType.USER
        )
    )
}

@Composable
private fun SearchHistoryDetailLayout(searchedText: String) {
    Text(
        text = searchedText,
        style = TextStyle(
            fontSize = 16.dp.toSp(),
            fontFamily = FontFamily(Font(R.font.pretendard_medium)),
            fontWeight = FontWeight(500),
            color = colorResource(id = R.color.gray_1_313131),
        ),
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 8.dp)
    )
}

@Composable
private fun SearchHistoryDeleteButton(
    onKeywordDeleteClick: (String, SearchHistoryType) -> Unit,
    searchHistoryDetail: SearchHistoryDetail
) {
    NoRippleIconButton(
        onClick = {
            with(searchHistoryDetail) {
                onKeywordDeleteClick(history, searchHistoryType)
            }
        },
        iconContentDescription = "History Delete Button",
        iconPainter = painterResource(id = R.drawable.ic_x_sign)
    )
}