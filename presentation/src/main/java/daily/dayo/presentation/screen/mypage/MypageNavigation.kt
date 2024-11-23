package daily.dayo.presentation.screen.mypage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import daily.dayo.presentation.screen.bookmark.BookmarkScreen

fun NavController.navigateProfileEdit() {
    navigate(MyPageRoute.profileEdit())
}

fun NavController.navigateBookmark() {
    navigate(MyPageRoute.bookmark())
}

fun NavGraphBuilder.myPageNavGraph(
    onBackClick: () -> Unit,
    onFollowButtonClick: (Int) -> Unit,
    onBookmarkClick: () -> Unit,
    onProfileEditClick: () -> Unit
) {
    composable(MyPageRoute.route) {
        MyPageScreen(
            onBackClick = onBackClick,
            onFollowButtonClick = onFollowButtonClick,
            onProfileEditClick = onProfileEditClick,
            onBookmarkClick = onBookmarkClick,
        )
    }

    composable(
        route = MyPageRoute.follow("{tabNum}"),
        arguments = listOf(
            navArgument("tabNum") {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->
        val tabNum = navBackStackEntry.arguments?.getInt("tabNum") ?: 0
        FollowScreen(
            tabNum = tabNum,
            onBackClick = onBackClick
        )
    }

    composable(MyPageRoute.profileEdit()) {
        MyPageEditScreen(
            onBackClick = onBackClick
        )
    }

    composable(MyPageRoute.bookmark()) {
        BookmarkScreen(
            onBackClick = onBackClick
        )
    }
}

object MyPageRoute {
    const val route = "myPage"

    fun follow(tabNum: String) = "$route/follow/$tabNum"
    fun profileEdit() = "$route/edit"
    fun bookmark() = "$route/bookmark"
    fun bookmarkPost(postId: String) = "$route/bookmark/$postId"
}