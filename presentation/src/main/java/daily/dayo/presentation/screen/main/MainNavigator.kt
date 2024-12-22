package daily.dayo.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import daily.dayo.presentation.screen.home.HomeRoute
import daily.dayo.presentation.screen.home.navigateHome
import daily.dayo.presentation.screen.mypage.navigateBookmark
import daily.dayo.presentation.screen.mypage.navigateFolderCreate
import daily.dayo.presentation.screen.mypage.navigateFolderEdit
import daily.dayo.presentation.screen.mypage.navigateProfileEdit
import daily.dayo.presentation.screen.post.navigatePost
import daily.dayo.presentation.screen.post.navigatePostLikeUsers
import daily.dayo.presentation.screen.search.navigateSearch
import daily.dayo.presentation.screen.search.navigateSearchPostHashtag
import daily.dayo.presentation.screen.search.navigateSearchResult
import daily.dayo.presentation.screen.write.navigateWrite
import daily.dayo.presentation.screen.write.navigateWriteFolder
import daily.dayo.presentation.screen.write.navigateWriteFolderNew
import daily.dayo.presentation.screen.write.navigateWriteTag

class MainNavigator(
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

    fun navigateSearchPostHashtag(hashtag: String) {
        navController.navigateSearchPostHashtag(hashtag = hashtag)
    }

    fun navigateProfileEdit() {
        navController.navigateProfileEdit()
    }

    fun navigateBookmark() {
        navController.navigateBookmark()
    }

    fun navigateFolderCreate() {
        navController.navigateFolderCreate()
    }

    fun navigateFolderEdit(folderId: String) {
        navController.navigateFolderEdit(folderId)
    }

    fun navigateWrite() {
        navController.navigateWrite()
    }

    fun navigateWriteTag() {
        navController.navigateWriteTag()
    }

    fun navigateWriteFolder() {
        navController.navigateWriteFolder()
    }

    fun navigateWriteFolderNew() {
        navController.navigateWriteFolderNew()
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

    fun navigatePostLikeUsers(postId: String) {
        navController.navigatePostLikeUsers(postId = postId)
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    @Composable
    fun shouldShowBottomBar(): Boolean {
        val currentRoute = currentDestination?.route ?: return false
        return currentRoute in Screen
    }

    fun isWriteGraph(): Boolean {
        return navController.currentDestination?.route?.startsWith("write") == true
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}