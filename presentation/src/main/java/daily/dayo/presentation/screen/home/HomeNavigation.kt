package daily.dayo.presentation.screen.home

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import daily.dayo.presentation.screen.main.Screen
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.homeNavGraph(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit
) {
    composable(Screen.Home.route) {
        HomeScreen(coroutineScope, bottomSheetState, bottomSheetContent)
    }
}
