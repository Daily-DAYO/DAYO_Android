package daily.dayo.presentation.screen.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.home.CategoryMenu
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.h1
import daily.dayo.presentation.view.CategoryHorizontalGroup
import daily.dayo.presentation.view.TopNavigation

@Composable
fun FeedScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    Text(
                        text = stringResource(id = R.string.feed),
                        modifier = Modifier.padding(start = 18.dp),
                        style = MaterialTheme.typography.h1.copy(
                            color = Gray1_313131
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // Category
            val categoryMenus = listOf(
                CategoryMenu.All,
                CategoryMenu.Scheduler,
                CategoryMenu.StudyPlanner,
                CategoryMenu.PocketBook,
                CategoryMenu.SixHoleDiary,
                CategoryMenu.Digital,
                CategoryMenu.ETC
            )
            val selectedCategory = remember { mutableStateOf(categoryMenus[0]) }
            CategoryHorizontalGroup(categoryMenus, selectedCategory, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
        }
    }
}