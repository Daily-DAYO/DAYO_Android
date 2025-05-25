package daily.dayo.presentation.screen.main

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import daily.dayo.presentation.R
import daily.dayo.presentation.screen.feed.FeedRoute
import daily.dayo.presentation.screen.feed.feedNavGraph
import daily.dayo.presentation.screen.home.HomeRoute
import daily.dayo.presentation.screen.home.homeNavGraph
import daily.dayo.presentation.screen.mypage.MyPageRoute
import daily.dayo.presentation.screen.mypage.myPageNavGraph
import daily.dayo.presentation.screen.mypage.navigateBackToFolder
import daily.dayo.presentation.screen.notice.noticeNavGraph
import daily.dayo.presentation.screen.notification.NotificationRoute
import daily.dayo.presentation.screen.notification.notificationNavGraph
import daily.dayo.presentation.screen.post.postNavGraph
import daily.dayo.presentation.screen.profile.profileNavGraph
import daily.dayo.presentation.screen.search.searchNavGraph
import daily.dayo.presentation.screen.settings.settingsNavGraph
import daily.dayo.presentation.screen.write.WriteRoute
import daily.dayo.presentation.screen.write.writeNavGraph
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState
import daily.dayo.presentation.viewmodel.NoticeViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val currentMemberId = profileViewModel.currentMemberId
    val coroutineScope = rememberCoroutineScope()
    val noticeViewModel = hiltViewModel<NoticeViewModel>()
    val snackBarHostState = remember { SnackbarHostState() }

    val bottomSheetState = getBottomSheetDialogState()
    var bottomSheetContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    SharedTransitionLayout {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
        ) {
            Scaffold(
                bottomBar = { bottomSheetContent?.invoke() }
            ) {
                Scaffold(
                    content = { innerPadding ->
                        Box(Modifier.padding(innerPadding)) {
                            NavHost(
                                navController = navigator.navController,
                                startDestination = Screen.Home.route
                            ) {
                                homeNavGraph(
                                    onPostClick = { navigator.navigatePost(it) },
                                    onProfileClick = { memberId ->
                                        navigator.navigateProfile(
                                            currentMemberId,
                                            memberId
                                        )
                                    },
                                    onSearchClick = { navigator.navigateSearch() },
                                    coroutineScope = coroutineScope,
                                    bottomSheetState = bottomSheetState,
                                    bottomSheetContent = { content ->
                                        bottomSheetContent = content
                                    },
                                )
                                feedNavGraph(
                                    snackBarHostState = snackBarHostState,
                                    onEmptyViewClick = { navigator.navigateHome() },
                                    onPostClick = { navigator.navigatePost(it) },
                                    onProfileClick = { memberId ->
                                        navigator.navigateProfile(
                                            currentMemberId,
                                            memberId
                                        )
                                    },
                                    onPostLikeUsersClick = { navigator.navigatePostLikeUsers(it) },
                                    onPostHashtagClick = { navigator.navigateSearchPostHashtag(it) },
                                    bottomSheetState = bottomSheetState,
                                    bottomSheetContent = { content -> bottomSheetContent = content }
                                )
                                postNavGraph(
                                    snackBarHostState = snackBarHostState,
                                    onProfileClick = { memberId ->
                                        navigator.navigateProfile(
                                            currentMemberId,
                                            memberId
                                        )
                                    },
                                    onPostLikeUsersClick = { navigator.navigatePostLikeUsers(it) },
                                    onPostHashtagClick = { navigator.navigateSearchPostHashtag(it) },
                                    onBackClick = { navigator.popBackStack() }
                                )
                                searchNavGraph(
                                    onBackClick = { navigator.popBackStack() },
                                    onSearch = { navigator.navigateSearchResult(it) },
                                    onPostClick = { navigator.navigatePost(it) },
                                    onProfileClick = { memberId ->
                                        navigator.navigateProfile(
                                            currentMemberId,
                                            memberId
                                        )
                                    },
                                )
                                writeNavGraph(
                                    snackBarHostState = snackBarHostState,
                                    navController = navigator.navController,
                                    onBackClick = { navigator.navigateUp() },
                                    onTagClick = { navigator.navigateWriteTag() },
                                    onWriteFolderClick = { navigator.navigateWriteFolder() },
                                    onWriteFolderNewClick = { navigator.navigateWriteFolderNew() },
                                    bottomSheetState = bottomSheetState,
                                    bottomSheetContent = { content ->
                                        bottomSheetContent = content
                                    },
                                )
                                myPageNavGraph(
                                    navController = navigator.navController,
                                    onBackClick = { navigator.popBackStack() },
                                    onSettingsClick = { navigator.navigateSettings() },
                                    onFollowButtonClick = { memberId, tabNum ->
                                        navigator.navigateFollowMenu(
                                            memberId,
                                            tabNum
                                        )
                                    },
                                    onProfileClick = { memberId ->
                                        navigator.navigateProfile(
                                            currentMemberId,
                                            memberId
                                        )
                                    },
                                    onProfileEditClick = { navigator.navigateProfileEdit() },
                                    onBookmarkClick = { navigator.navigateBookmark() },
                                    onFolderClick = { folderId -> navigator.navigateFolder(folderId) },
                                    onFolderCreateClick = { navigator.navigateFolderCreate() },
                                    onFolderEditClick = { folderId ->
                                        navigator.navigateFolderEdit(
                                            folderId
                                        )
                                    },
                                    onPostClick = { postId -> navigator.navigatePost(postId) },
                                    onPostMoveClick = { folderId ->
                                        navigator.navigateFolderPostMove(
                                            folderId
                                        )
                                    },
                                    navigateBackToFolder = { folderId ->
                                        navigator.navigateBackToFolder(
                                            folderId
                                        )
                                    }
                                )
                                profileNavGraph(
                                    snackBarHostState = snackBarHostState,
                                    onFollowMenuClick = { memberId, tabNum ->
                                        navigator.navigateFollowMenu(
                                            memberId,
                                            tabNum
                                        )
                                    },
                                    onFolderClick = { folderId -> navigator.navigateFolder(folderId) },
                                    onPostClick = { postId -> navigator.navigatePost(postId) },
                                    onBackClick = { navigator.popBackStack() }
                                )
                                notificationNavGraph(
                                    onPostClick = { navigator.navigatePost(it) },
                                    onProfileClick = { memberId ->
                                        navigator.navigateProfile(
                                            currentMemberId,
                                            memberId
                                        )
                                    },
                                    onNoticeClick = { noticeId ->
                                        navigator.navigateNoticeDetail(
                                            noticeId
                                        )
                                    },
                                )
                                settingsNavGraph(
                                    coroutineScope = coroutineScope,
                                    snackBarHostState = snackBarHostState,
                                    onProfileEditClick = { navigator.navigateProfileEdit() },
                                    onWithdrawClick = { navigator.navigateWithdraw() },
                                    onBlockUsersClick = { navigator.navigateBlockedUsers() },
                                    onPasswordChangeClick = { navigator.navigateChangePassword() },
                                    onSettingNotificationClick = { navigator.navigateSettingsNotification() },
                                    onNoticesClick = { navigator.navigateNotices() },
                                    onBackClick = { navigator.popBackStack() },
                                    bottomSheetState = bottomSheetState,
                                    bottomSheetContent = { content ->
                                        bottomSheetContent = content
                                    }
                                )
                                noticeNavGraph(
                                    noticeViewModel = noticeViewModel,
                                    onBackClick = { navigator.popBackStack() },
                                    onNoticeDetailClick = { noticeId ->
                                        navigator.navigateNoticeDetail(
                                            noticeId
                                        )
                                    },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                )
                            }
                        }
                    },
                    bottomBar = {
                        MainBottomNavigation(
                            visible = navigator.shouldShowBottomBar(),
                            navController = navigator.navController
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun MainBottomNavigation(
    visible: Boolean,
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val items = listOf(
        Screen.Home,
        Screen.Feed,
        Screen.Write,
        Screen.Notification,
        Screen.MyPage
    )

    if (visible) {
        BottomNavigation(
            backgroundColor = White_FFFFFF,
            contentColor = Gray2_767B83,
            modifier = Modifier.height(73.dp)
        ) {
            items.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                BottomNavigationItem(
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = if (selected) screen.selectedIcon else screen.defaultIcon),
                                contentDescription = stringResource(id = screen.resourceId),
                                modifier = Modifier
                                    .size(if (screen.route != Screen.Write.route) 24.dp else 36.dp)
                            )

                            if (screen.route != Screen.Write.route) {
                                Text(text = stringResource(screen.resourceId), style = DayoTheme.typography.caption5)
                            }
                        }
                    },
                    selected = selected,
                    selectedContentColor = Dark,
                    onClick = {
                        navController.navigate(screen.route) {
                            if (screen.route != Screen.Write.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                            launchSingleTop =
                                if (screen.route == Screen.Write.route) false else true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val defaultIcon: Int, @DrawableRes val selectedIcon: Int) {
    object Home : Screen(HomeRoute.route, R.string.home, R.drawable.ic_home, R.drawable.ic_home_filled)
    object Feed : Screen(FeedRoute.route, R.string.feed, R.drawable.ic_feed, R.drawable.ic_feed_filled)
    object Write : Screen(WriteRoute.route, R.string.write, R.drawable.ic_write, R.drawable.ic_write_filled)
    object Notification : Screen(NotificationRoute.route, R.string.notification, R.drawable.ic_notification, R.drawable.ic_notification_filled)
    object MyPage : Screen(MyPageRoute.route, R.string.my_page, R.drawable.ic_my_page, R.drawable.ic_my_page_filled)

    companion object {
        operator fun contains(route: String): Boolean {
            return listOf(Home, Feed, Notification, MyPage).map { it.route }.contains(route)
        }
    }
}
