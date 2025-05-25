package daily.dayo.presentation.screen.profile

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavController.navigateProfile(memberId: String) {
    navigate(ProfileRoute.profile(memberId))
}

fun NavGraphBuilder.profileNavGraph(
    snackBarHostState: SnackbarHostState,
    onFollowMenuClick: (String, Int) -> Unit,
    onFolderClick: (Long) -> Unit,
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
            externalSnackBarHostState = snackBarHostState,
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
