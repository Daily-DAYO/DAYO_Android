package daily.dayo.presentation.screen.settings

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import daily.dayo.presentation.screen.account.WithdrawScreen

fun NavController.navigateSettings() {
    navigate(SettingsRoute.route)
}

fun NavController.navigateWithdraw() {
    navigate(SettingsRoute.withdraw)
}

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.settingsNavGraph(
    onProfileEditClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onBackClick: () -> Unit,
    snackBarHostState: SnackbarHostState,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
) {
    composable(SettingsRoute.route) {
        SettingsScreen(
            onProfileEditClick = onProfileEditClick,
            onWithdrawClick = onWithdrawClick,
            onBackClick = onBackClick
        )
    }

    composable(SettingsRoute.withdraw) {
        WithdrawScreen(
            onBackClick = onBackClick,
            snackBarHostState = snackBarHostState,
            bottomSheetState = bottomSheetState,
            bottomSheetContent = bottomSheetContent
        )
    }
}

object SettingsRoute {
    const val route = "settings"

    const val withdraw = "${route}/withdraw"
}
