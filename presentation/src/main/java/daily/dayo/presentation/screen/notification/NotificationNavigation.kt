package daily.dayo.presentation.screen.notification

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.notificationNavGraph(
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onNoticeClick: (Long) -> Unit,
) {
    composable(NotificationRoute.route) {
        NotificationScreen(
            onPostClick = onPostClick,
            onProfileClick = onProfileClick,
            onNoticeClick = onNoticeClick,
        )
    }
}

object NotificationRoute {
    const val route = "notification"
}