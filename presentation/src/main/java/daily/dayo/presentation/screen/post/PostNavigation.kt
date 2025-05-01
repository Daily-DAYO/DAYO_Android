package daily.dayo.presentation.screen.post

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavController.navigatePost(postId: Long) {
    navigate(PostRoute.postDetail(postId))
}

fun NavController.navigatePostLikeUsers(postId: Long) {
    navigate(PostRoute.postLikeUsers(postId))
}

fun NavGraphBuilder.postNavGraph(
    snackBarHostState: SnackbarHostState,
    onProfileClick: (String) -> Unit,
    onPostLikeUsersClick: (Long) -> Unit,
    onPostHashtagClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    composable(
        route = PostRoute.postDetailRoute,
        arguments = listOf(
            navArgument("postId") {
                type = NavType.LongType
            }
        )
    ) { navBackStackEntry ->
        navBackStackEntry.arguments?.getLong("postId")?.let { postId ->
            PostScreen(
                postId = postId,
                snackBarHostState = snackBarHostState,
                onProfileClick = onProfileClick,
                onPostLikeUsersClick = onPostLikeUsersClick,
                onPostHashtagClick = onPostHashtagClick,
                onBackClick = onBackClick
            )
        }
    }

    composable(
        route = PostRoute.postLikeUsersRoute,
        arguments = listOf(
            navArgument("postId") {
                type = NavType.LongType
            }
        )
    ) { navBackStackEntry ->
        navBackStackEntry.arguments?.getLong("postId")?.let { postId ->
            PostLikeUsersScreen(postId = postId, onBackClick = { onBackClick() }, onProfileClick = onProfileClick)
        }
    }
}

object PostRoute {
    private const val route: String = "post"
    const val postDetailRoute = "$route/{postId}"
    const val postLikeUsersRoute = "$route/likeUsers/{postId}"

    fun postDetail(postId: Long) = "$route/$postId"
    fun postLikeUsers(postId: Long) = "$route/likeUsers/$postId"
}