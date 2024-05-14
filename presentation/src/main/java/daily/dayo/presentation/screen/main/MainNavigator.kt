package daily.dayo.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import daily.dayo.presentation.screen.home.HomeRoute
import daily.dayo.presentation.screen.home.navigateHome
import daily.dayo.presentation.screen.post.navigatePost
import daily.dayo.presentation.screen.search.navigateSearch
import daily.dayo.presentation.screen.search.navigateSearchResult

internal class MainNavigator(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    fun navigateHome() {
        navController.navigateHome()
    }

    fun navigatePost(postId: String) {
        navController.navigatePost(postId = postId)
    }

    fun navigateSearch() {
        navController.navigateSearch()
    }

    fun navigateSearchResult(searchKeyword: String) {
        navController.navigateSearchResult(searchKeyword)
    }

    fun popBackStack() {
        navController.popBackStack()
    }

    fun popBackStackIfNotHome() {
        if (!isSameCurrentDestination(HomeRoute.route)) {
            navController.popBackStack()
        }
    }

    private fun isSameCurrentDestination(route: String) =
        navController.currentDestination?.route == route

    @Composable
    fun shouldShowBottomBar(): Boolean {
        val currentRoute = currentDestination?.route ?: return false
        return currentRoute in Screen
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}