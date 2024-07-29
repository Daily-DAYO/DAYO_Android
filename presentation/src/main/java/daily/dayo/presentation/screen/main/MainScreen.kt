package daily.dayo.presentation.screen.main

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import daily.dayo.presentation.screen.post.postNavGraph
import daily.dayo.presentation.screen.search.searchNavGraph
import daily.dayo.presentation.screen.write.WriteRoute
import daily.dayo.presentation.screen.write.writeNavGraph
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val bottomSheetState = getBottomSheetDialogState()
    var bottomSheet: (@Composable () -> Unit)? by remember { mutableStateOf(null) }
    val bottomSheetContent: (@Composable () -> Unit) -> Unit = {
        bottomSheet = it
    }

    Scaffold(
        bottomBar = { bottomSheet?.let { it() } }
    ) {
        Scaffold(
            content = { innerPadding ->
                Box(Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navigator.navController,
                        startDestination = Screen.Home.route
                    ) {
                        homeNavGraph(
                            coroutineScope,
                            bottomSheetState,
                            bottomSheetContent,
                            onSearchClick = { navigator.navigateSearch() }
                        )
                        feedNavGraph(
                            snackBarHostState = snackBarHostState,
                            onEmptyViewClick = { navigator.navigateHome() },
                            onPostClick = { navigator.navigatePost(it) },
                            onPostLikeUsersClick = { navigator.navigatePostLikeUsers(it) },
                            onPostHashtagClick = { navigator.navigateSearchPostHashtag(it) }
                        )
                        postNavGraph(onBackClick = { navigator.popBackStack() })
                        searchNavGraph(
                            onBackClick = { navigator.popBackStack() },
                            onSearch = { navigator.navigateSearchResult(it) },
                            onPostClick = { navigator.navigatePost(it) }
                        )
                        writeNavGraph (
                            onBackClick = { navigator.popBackStack() },
                        )
                    }
                }
            },
            bottomBar = {
                MainBottomNavigation(
                    visible = navigator.shouldShowBottomBar(),
                    navController = navigator.navController
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
        )
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
                                Text(text = stringResource(screen.resourceId), style = MaterialTheme.typography.caption)
                            }
                        }
                    },
                    selected = selected,
                    selectedContentColor = Gray1_313131,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
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
    object Notification : Screen("notification", R.string.notification, R.drawable.ic_notification, R.drawable.ic_notification_filled)
    object MyPage : Screen("mypage", R.string.my_page, R.drawable.ic_my_page, R.drawable.ic_my_page_filled)

    companion object {
        operator fun contains(route: String): Boolean {
            return listOf(Home, Feed, Notification, MyPage).map { it.route }.contains(route)
        }
    }
}