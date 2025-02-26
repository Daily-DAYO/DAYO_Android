package daily.dayo.presentation.screen.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavController.navigateSettings() {
    navigate(SettingsRoute.route)
}

fun NavGraphBuilder.settingsNavGraph(
    onProfileEditClick: () -> Unit,
    onBackClick: () -> Unit
) {
    composable(SettingsRoute.route) {
        SettingsScreen(
            onProfileEditClick = onProfileEditClick,
            onBackClick = onBackClick
        )
    }
}

object SettingsRoute {
    const val route = "settings"
}
