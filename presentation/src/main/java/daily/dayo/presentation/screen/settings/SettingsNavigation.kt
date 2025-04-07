package daily.dayo.presentation.screen.settings

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope

fun NavController.navigateSettings() {
    navigate(SettingsRoute.route)
}

fun NavController.navigateChangePassword() {
    navigate(SettingsRoute.changePassword)
}

fun NavGraphBuilder.settingsNavGraph(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    onProfileEditClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable(SettingsRoute.route) {
        SettingsScreen(
            onProfileEditClick = onProfileEditClick,
            onPasswordChangeClick = onPasswordChangeClick,
            onBackClick = onBackClick
        )
    }

    composable(SettingsRoute.changePassword) {
        ChangePasswordScreen(
            coroutineScope = coroutineScope,
            snackBarHostState = snackBarHostState,
            onBackClick = onBackClick,
        )
    }
}

object SettingsRoute {
    const val route = "settings"

    const val changePassword = "${route}/changePassword"
}
