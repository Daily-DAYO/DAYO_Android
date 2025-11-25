package daily.dayo.presentation.screen.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavController.navigateHome() {
    this.navigate(HomeRoute.route)
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeNavGraph(
    onPostClick: (Long) -> Unit,
    onProfileClick: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    composable(HomeRoute.route) {
        HomeScreen(
            onPostClick = onPostClick,
            onProfileClick = onProfileClick,
            onSearchClick = onSearchClick
        )
    }
}

object HomeRoute {
    const val route = "home"
}
