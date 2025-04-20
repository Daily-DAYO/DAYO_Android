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

fun NavController.navigateSettingsNotification() {
    navigate(SettingsRoute.notification)
}

fun NavGraphBuilder.settingsNavGraph(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    onProfileEditClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingNotificationClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    composable(SettingsRoute.route) {
        SettingsScreen(
            onProfileEditClick = onProfileEditClick,
            onBackClick = onBackClick,
            onPasswordChangeClick = onPasswordChangeClick,
            onSettingNotificationClick = onSettingNotificationClick,
        )
    }

    composable(route = SettingsRoute.notification) {
        SettingNotificationScreen(
            onBackClick = onBackClick,
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
    const val notification = "${route}/notification"
}