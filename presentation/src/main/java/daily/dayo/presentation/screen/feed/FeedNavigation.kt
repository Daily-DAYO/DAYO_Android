package daily.dayo.presentation.screen.feed

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.feedNavGraph(
    onEmptyViewClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit
) {
    composable(FeedRoute.route) {
        FeedScreen(
            onEmptyViewClick = onEmptyViewClick,
            onPostClick = onPostClick,
            onPostLikeUsersClick = onPostLikeUsersClick,
            onPostHashtagClick = onPostHashtagClick
        )
    }
}

object FeedRoute {
    const val route = "feed"
}