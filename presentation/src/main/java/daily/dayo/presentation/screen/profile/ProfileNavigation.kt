package daily.dayo.presentation.screen.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavController.navigateProfile(memberId: String) {
    navigate(ProfileRoute.profile(memberId))
}

fun NavGraphBuilder.profileNavGraph(
    onFollowMenuClick: (String, Int) -> Unit,
    onFolderClick: (String) -> Unit,
    onPostClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    composable(
        route = ProfileRoute.profile("{memberId}"),
        arguments = listOf(
            navArgument("memberId") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val memberId = navBackStackEntry.arguments?.getString("memberId") ?: ""
        ProfileScreen(
            memberId = memberId,
            onFollowMenuClick = onFollowMenuClick,
            onFolderClick = onFolderClick,
            onBackClick = onBackClick
        )
    }
}

object ProfileRoute {
    const val route = "profile"
    fun profile(memberId: String) = "$route/$memberId"
}
