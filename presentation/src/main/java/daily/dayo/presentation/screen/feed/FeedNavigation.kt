package daily.dayo.presentation.screen.feed

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.feedNavGraph(
    snackBarHostState: SnackbarHostState,
    onEmptyViewClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
) {
    composable(FeedRoute.route) {
        FeedScreen(
            snackBarHostState = snackBarHostState,
            onEmptyViewClick = onEmptyViewClick,
            onPostClick = onPostClick,
            onProfileClick = onProfileClick,
            onPostLikeUsersClick = onPostLikeUsersClick,
            onPostHashtagClick = onPostHashtagClick,
            bottomSheetState = bottomSheetState,
            bottomSheetContent = bottomSheetContent
        )
    }
}

object FeedRoute {
    const val route = "feed"
}