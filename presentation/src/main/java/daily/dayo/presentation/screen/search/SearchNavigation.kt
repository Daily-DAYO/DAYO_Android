package daily.dayo.presentation.screen.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavController.navigateSearch() {
    navigate(SearchRoute.route)
}

fun NavController.navigateSearchResult(searchKeyword: String) {
    navigate(SearchRoute.resultSearch(searchKeyword))
}

fun NavController.navigateSearchPostHashtag(hashtag: String) {
    navigate(SearchRoute.searchHashtag(hashtag))
}

fun NavGraphBuilder.searchNavGraph(
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit,
    onPostClick: (String) -> Unit
) {
    composable(route = SearchRoute.route) {
        SearchRoute(
            onBackClick = onBackClick,
            onSearch = onSearch
        )
    }

    composable(
        route = SearchRoute.resultSearch("{searchKeyword}"),
        arguments = listOf(navArgument("searchKeyword") { type = NavType.StringType })
    ) { backStackEntry ->
        val searchKeyword = backStackEntry.arguments?.getString("searchKeyword") ?: ""
        SearchResultRoute(
            searchKeyword = searchKeyword,
            onBackClick = onBackClick,
            onPostClick = onPostClick
        )
    }

    composable(
        route = SearchRoute.searchHashtag("{hashtag}"),
        arguments = listOf(
            navArgument("hashtag") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val hashtag = navBackStackEntry.arguments?.getString("hashtag") ?: ""
        SearchPostHashtagScreen(
            hashtag = hashtag,
            onBackClick = onBackClick,
            onPostClick = onPostClick
        )
    }
}

object SearchRoute {
    const val route = "search"

    fun resultSearch(searchKeyword: String) = "$route/$searchKeyword"
    fun searchHashtag(hashtag: String) = "$route/hashtag/$hashtag"
}