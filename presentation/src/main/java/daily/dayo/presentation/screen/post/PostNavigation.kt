package daily.dayo.presentation.screen.post

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

fun NavGraphBuilder.postNavGraph() {
    composable(
        route = PostRoute.postDetail("{postId}"),
        arguments = listOf(
            navArgument("postId") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val postId = navBackStackEntry.arguments?.getString("postId") ?: ""
        PostScreen(postId)
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
        PostLikeUsersScreen(postId = postId, {}, {})
    }
}

object PostRoute {
    private const val route: String = "post"
    fun postDetail(postId: String): String = "$route/$postId"
    fun postLikeUsers(postId: String): String = "$route/likeUsers/$postId"
}