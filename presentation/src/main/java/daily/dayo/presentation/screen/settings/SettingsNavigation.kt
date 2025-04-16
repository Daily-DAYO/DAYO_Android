package daily.dayo.presentation.screen.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavController.navigateSettings() {
    navigate(SettingsRoute.route)
}

fun NavController.navigateSettingsNotification() {
    navigate(SettingsRoute.notification)
}

fun NavGraphBuilder.settingsNavGraph(
    onProfileEditClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingNotificationClick: () -> Unit,
) {
    composable(SettingsRoute.route) {
        SettingsScreen(
            onProfileEditClick = onProfileEditClick,
            onBackClick = onBackClick,
            onSettingNotificationClick = onSettingNotificationClick,
        )
    }

    composable(route = SettingsRoute.notification) {
        SettingNotificationScreen(
            onBackClick = onBackClick
        )
    }
}

object SettingsRoute {
    const val route = "settings"

    const val notification = "${route}/notification"
}