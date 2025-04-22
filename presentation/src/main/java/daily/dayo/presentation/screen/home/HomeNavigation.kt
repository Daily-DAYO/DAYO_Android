package daily.dayo.presentation.screen.home

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope

fun NavController.navigateHome() {
    this.navigate(HomeRoute.route)
}

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.homeNavGraph(
    onPostClick: (Long) -> Unit,
    onProfileClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit
) {
    composable(HomeRoute.route) {
        HomeScreen(
            onPostClick = onPostClick,
            onProfileClick = onProfileClick,
            onSearchClick = onSearchClick,
            coroutineScope = coroutineScope,
            bottomSheetState = bottomSheetState,
            bottomSheetContent = bottomSheetContent
        )
    }
}

object HomeRoute {
    const val route = "home"
}
