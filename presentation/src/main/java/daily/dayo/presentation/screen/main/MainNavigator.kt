package daily.dayo.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import daily.dayo.presentation.screen.home.HomeRoute
import daily.dayo.presentation.screen.home.navigateHome
import daily.dayo.presentation.screen.mypage.navigateBackToFolder
import daily.dayo.presentation.screen.mypage.navigateBlockedUsers
import daily.dayo.presentation.screen.mypage.navigateBookmark
import daily.dayo.presentation.screen.mypage.navigateFolder
import daily.dayo.presentation.screen.mypage.navigateFolderCreate
import daily.dayo.presentation.screen.mypage.navigateFolderEdit
import daily.dayo.presentation.screen.mypage.navigateFolderPostMove
import daily.dayo.presentation.screen.mypage.navigateFollowMenu
import daily.dayo.presentation.screen.mypage.navigateMyPage
import daily.dayo.presentation.screen.mypage.navigateProfileEdit
import daily.dayo.presentation.screen.notice.navigateNotices
import daily.dayo.presentation.screen.notice.navigateNoticeDetail
import daily.dayo.presentation.screen.post.navigatePost
import daily.dayo.presentation.screen.post.navigatePostLikeUsers
import daily.dayo.presentation.screen.profile.navigateProfile
import daily.dayo.presentation.screen.rules.RuleType
import daily.dayo.presentation.screen.rules.navigateRules
import daily.dayo.presentation.screen.search.navigateSearch
import daily.dayo.presentation.screen.search.navigateSearchPostHashtag
import daily.dayo.presentation.screen.search.navigateSearchResult
import daily.dayo.presentation.screen.settings.navigateChangePassword
import daily.dayo.presentation.screen.settings.navigateInformation
import daily.dayo.presentation.screen.settings.navigateSettings
import daily.dayo.presentation.screen.settings.navigateSettingsNotification
import daily.dayo.presentation.screen.settings.navigateWithdraw
import daily.dayo.presentation.screen.write.navigatePostEdit
import daily.dayo.presentation.screen.write.navigateToWritePostWithFolder
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

    fun navigateToBottomTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToBottomTabWithClearStack(route: String) {
        // 전체 백스택을 정리하고 탭 이동
        
        // 현재 destination과 다른 경우에만 navigation 수행
        if (navController.currentDestination?.route != route) {
            navController.popBackStack(navController.graph.findStartDestination().id, false)
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    }

    fun navigatePost(postId: Long) {
        navController.navigatePost(postId = postId)
    }

    fun navigateProfile(currentMemberId: String?, memberId: String) {
        if (currentMemberId != null) {
            if (currentMemberId == memberId) {
                navController.navigateMyPage()
            } else {
                navController.navigateProfile(memberId)
            }
        }
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

    fun navigateNotices() {
        navController.navigateNotices()
    }

    fun navigateNoticeDetail(noticeId: Long) {
        navController.navigateNoticeDetail(noticeId = noticeId)
    }

    fun navigateInformation() {
        navController.navigateInformation()
    }

    fun navigateRules(ruleType: RuleType) {
        navController.navigateRules(ruleType)
    }

    fun navigateSettings() {
        navController.navigateSettings()
    }

    fun navigateChangePassword() {
        navController.navigateChangePassword()
    }

    fun navigateSettingsNotification() {
        navController.navigateSettingsNotification()
    }

    fun navigateProfileEdit() {
        navController.navigateProfileEdit()
    }

    fun navigateWithdraw() {
        navController.navigateWithdraw()
    }

    fun navigateBlockedUsers() {
        navController.navigateBlockedUsers()
    }

    fun navigateBookmark() {
        navController.navigateBookmark()
    }

    fun navigateFolderCreate() {
        navController.navigateFolderCreate()
    }

    fun navigateFolderEdit(folderId: Long) {
        navController.navigateFolderEdit(folderId)
    }

    fun navigateFolderPostMove(folderId: Long) {
        navController.navigateFolderPostMove(folderId)
    }

    fun navigateFollowMenu(memberId: String, tabNum: Int) {
        navController.navigateFollowMenu(memberId, tabNum)
    }

    fun navigateFolder(folderId: Long) {
        navController.navigateFolder(folderId)
    }

    fun navigateBackToFolder(folderId: Long) {
        navController.navigateBackToFolder(folderId)
    }

    fun navigateWrite() {
        navController.navigateWrite()
    }

    fun navigatePostEdit(postId: Long) {
        navController.navigatePostEdit(postId)
    }

    fun navigateToWritePostWithFolder(folderId: Long) {
        navController.navigateToWritePostWithFolder(folderId)
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

    fun navigatePostLikeUsers(postId: Long) {
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