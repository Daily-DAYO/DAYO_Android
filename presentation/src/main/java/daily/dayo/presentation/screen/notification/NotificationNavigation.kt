package daily.dayo.presentation.screen.notification

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.notificationNavGraph() {
    composable(NotificationRoute.route) {
        NotificationScreen()
    }
}

object NotificationRoute {
    const val route = "notification"
}