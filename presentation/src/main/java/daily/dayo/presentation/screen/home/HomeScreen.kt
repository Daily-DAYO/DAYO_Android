package daily.dayo.presentation.screen.home

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import daily.dayo.domain.model.Category
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TextButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.BottomSheetDialog
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState
import daily.dayo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val HOME_DAYOPICK_PAGE_TAB_ID = 0
const val HOME_NEW_PAGE_TAB_ID = 1

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
    onSearchClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    var homeTabState by rememberSaveable { mutableIntStateOf(HOME_DAYOPICK_PAGE_TAB_ID) }
    var selectedCategory by rememberSaveable { mutableStateOf(Pair(CategoryMenu.All.name, 0)) } // name, index
    val onClickCategory: (CategoryMenu, Int) -> Unit = { categoryMenu, index ->
        selectedCategory = Pair(categoryMenu.name, index)
        homeViewModel.setCategory(categoryMenu.category)
        coroutineScope.launch { bottomSheetState.hide() }
    }

    BackHandler(enabled = bottomSheetState.isVisible) {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }

    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(start = 18.dp)
                    ) {
                        TextButton(
                            onClick = {
                                homeTabState = HOME_DAYOPICK_PAGE_TAB_ID
                            },
                            text = stringResource(id = R.string.DayoPick),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                color = if (homeTabState == HOME_DAYOPICK_PAGE_TAB_ID) Dark else Gray5_E8EAEE,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        TextButton(
                            onClick = {
                                homeTabState = HOME_NEW_PAGE_TAB_ID
                            },
                            text = stringResource(id = R.string.New),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                color = if (homeTabState == HOME_NEW_PAGE_TAB_ID) Dark else Gray5_E8EAEE,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                },
                rightIcon = {
                    NoRippleIconButton(
                        onClick = { onSearchClick() },
                        iconContentDescription = "search button",
                        iconPainter = painterResource(id = R.drawable.ic_search),
                        iconModifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp),
                        iconTintColor = Dark
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (homeTabState) {
                HOME_DAYOPICK_PAGE_TAB_ID -> {
                    HomeDayoPickScreen(selectedCategory.first, coroutineScope, bottomSheetState, homeViewModel)
                    homeViewModel.loadDayoPickPosts()
                }

                HOME_NEW_PAGE_TAB_ID -> {
                    HomeNewScreen(selectedCategory.first, coroutineScope, bottomSheetState, homeViewModel)
                    homeViewModel.loadNewPosts()
                }
            }
        }
    }

    bottomSheetContent {
        CategoryBottomSheetDialog(onClickCategory, selectedCategory, coroutineScope, bottomSheetState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryBottomSheetDialog(
    onCategorySelected: (CategoryMenu, Int) -> Unit,
    selectedCategory: Pair<String, Int>,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    val categoryMenus = listOf(
        CategoryMenu.All,
        CategoryMenu.Scheduler,
        CategoryMenu.StudyPlanner,
        CategoryMenu.PocketBook,
        CategoryMenu.SixHoleDiary,
        CategoryMenu.Digital,
        CategoryMenu.ETC
    )

    BottomSheetDialog(
        sheetState = bottomSheetState,
        buttons = categoryMenus.mapIndexed { index, category ->
            Pair(category.name) {
                onCategorySelected(category, index)
            }
        },
        title = stringResource(id = R.string.filter),
        leftIconButtons = categoryMenus.map {
            ImageVector.vectorResource(it.defaultIcon)
        },
        leftIconCheckedButtons = categoryMenus.map {
            ImageVector.vectorResource(it.checkedIcon)
        },
        normalColor = Gray2_767B83,
        checkedColor = Primary_23C882,
        checkedButtonIndex = selectedCategory.second,
        closeButtonAction = { coroutineScope.launch { bottomSheetState.hide() } }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview(showBackground = true)
private fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen(rememberCoroutineScope(), getBottomSheetDialogState(), {}, onSearchClick = {})
    }
}


sealed class CategoryMenu(val name: String, @DrawableRes val defaultIcon: Int, @DrawableRes val checkedIcon: Int, val category: Category) {
    object All : CategoryMenu("전체", R.drawable.ic_category_all, R.drawable.ic_category_all_checked, Category.ALL)
    object Scheduler : CategoryMenu("스케줄러", R.drawable.ic_category_scheduler, R.drawable.ic_category_scheduler_checked, Category.SCHEDULER)
    object StudyPlanner : CategoryMenu("스터디 플래너", R.drawable.ic_category_studyplanner, R.drawable.ic_category_studyplanner_checked, Category.STUDY_PLANNER)
    object PocketBook : CategoryMenu("포켓북", R.drawable.ic_category_pocketbook, R.drawable.ic_category_pocketbook_checked, Category.POCKET_BOOK)
    object SixHoleDiary : CategoryMenu("6공 다이어리", R.drawable.ic_category_sixholediary, R.drawable.ic_category_sixholediary_checked, Category.SIX_DIARY)
    object Digital : CategoryMenu("모바일 다이어리", R.drawable.ic_category_digital, R.drawable.ic_category_digital_checked, Category.GOOD_NOTE)
    object ETC : CategoryMenu("기타", R.drawable.ic_category_etc, R.drawable.ic_category_etc_checked, Category.STUDY_PLANNER)
}
