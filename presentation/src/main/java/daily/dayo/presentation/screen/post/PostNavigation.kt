package daily.dayo.presentation.screen.post

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavController.navigatePost(postId: String) {
    navigate(PostRoute.postDetail(postId))
}

fun NavController.navigatePostLikeUsers(postId: String) {
    navigate(PostRoute.postLikeUsers(postId))
}

fun NavGraphBuilder.postNavGraph(
    snackBarHostState: SnackbarHostState,
    onProfileClick: (String) -> Unit,
    onPostLikeUsersClick: (String) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    composable(
        route = PostRoute.postDetail("{postId}"),
        arguments = listOf(
            navArgument("postId") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val postId = navBackStackEntry.arguments?.getString("postId") ?: ""
        PostScreen(
            postId = postId,
            snackBarHostState = snackBarHostState,
            onProfileClick = onProfileClick,
            onPostLikeUsersClick = onPostLikeUsersClick,
            onPostHashtagClick = onPostHashtagClick,
            onBackClick = onBackClick
        )
    }

    composable(
        route = PostRoute.postLikeUsers("{postId}"),
        arguments = listOf(
            navArgument("postId") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val postId = navBackStackEntry.arguments?.getString("postId") ?: ""
        PostLikeUsersScreen(postId = postId, onBackClick = { onBackClick() }, onProfileClick = {})
    }
}

object PostRoute {
    private const val route: String = "post"
    fun postDetail(postId: String): String = "$route/$postId"
    fun postLikeUsers(postId: String): String = "$route/likeUsers/$postId"
}