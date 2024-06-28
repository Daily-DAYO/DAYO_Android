package daily.dayo.presentation.screen.feed

import androidx.compose.material.ScaffoldState
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.feedNavGraph(
    snackBarHostState: SnackbarHostState,
    onEmptyViewClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit
) {
    composable(FeedRoute.route) {
        FeedScreen(
            snackBarHostState = snackBarHostState,
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