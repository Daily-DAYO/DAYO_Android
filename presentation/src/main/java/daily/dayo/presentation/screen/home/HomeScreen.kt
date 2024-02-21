package daily.dayo.presentation.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import daily.dayo.presentation.R
import daily.dayo.presentation.fragment.home.HOME_DAYOPICK_PAGE_TAB_ID
import daily.dayo.presentation.fragment.home.HOME_NEW_PAGE_TAB_ID
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.view.BottomSheetDialog
import daily.dayo.presentation.view.TextButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.getBottomSheetDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit
) {
    var homeTabState by rememberSaveable { mutableIntStateOf(HOME_DAYOPICK_PAGE_TAB_ID) }
    var selectedCategory by rememberSaveable { mutableStateOf(Pair(CategoryMenu.All.name, 0)) } // name, index
    val onCategorySelected: (String, Int) -> Unit = { category, index ->
        selectedCategory = Pair(category, index)
        coroutineScope.launch { bottomSheetState.hide() }
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
                                color = if (homeTabState == HOME_DAYOPICK_PAGE_TAB_ID) Gray1_313131 else Gray5_E8EAEE,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        TextButton(
                            onClick = {
                                homeTabState = HOME_NEW_PAGE_TAB_ID
                            },
                            text = stringResource(id = R.string.New),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                color = if (homeTabState == HOME_NEW_PAGE_TAB_ID) Gray1_313131 else Gray5_E8EAEE,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                },
                rightIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_search),
                        contentDescription = "search",
                        tint = Gray1_313131,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            HomeDayoPickScreen(selectedCategory.first, coroutineScope, bottomSheetState)
        }
    }

    bottomSheetContent {
        CategoryBottomSheetDialog(onCategorySelected, selectedCategory, coroutineScope, bottomSheetState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryBottomSheetDialog(
    onCategorySelected: (String, Int) -> Unit,
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
                onCategorySelected(category.name, index)
            }
        },
        title = stringResource(id = R.string.filter),
        leftIconButtons = categoryMenus.map {
            ImageVector.vectorResource(it.defaultIcon)
        },
        checkedButtonIndex = selectedCategory.second,
        closeButtonAction = { coroutineScope.launch { bottomSheetState.hide() } }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview(showBackground = true)
private fun PreviewHomeScreen() {
    MaterialTheme {
        HomeScreen(rememberNavController(), rememberCoroutineScope(), getBottomSheetDialogState(), {})
    }
}


sealed class CategoryMenu(val name: String, @DrawableRes val defaultIcon: Int) {
    object All : CategoryMenu("전체", R.drawable.ic_category_all)
    object Scheduler : CategoryMenu("스케줄러", R.drawable.ic_category_scheduler)
    object StudyPlanner : CategoryMenu("스터디 플래너", R.drawable.ic_category_studyplanner)
    object PocketBook : CategoryMenu("포켓북", R.drawable.ic_category_pocketbook)
    object SixHoleDiary : CategoryMenu("6공 다이어리", R.drawable.ic_category_sixholediary)
    object Digital : CategoryMenu("모바일 다이어리", R.drawable.ic_category_digital)
    object ETC : CategoryMenu("기타", R.drawable.ic_category_etc)
}
