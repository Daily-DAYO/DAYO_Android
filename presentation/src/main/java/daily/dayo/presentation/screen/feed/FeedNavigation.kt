package daily.dayo.presentation.screen.feed

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.feedNavGraph(
    onEmptyViewClick: () -> Unit,
    onPostClick: (String) -> Unit
) {
    composable(FeedRoute.route) {
        FeedScreen(
            onEmptyViewClick = onEmptyViewClick,
            onPostClick = onPostClick
        )
    }
}

object FeedRoute {
    const val route = "feed"
}