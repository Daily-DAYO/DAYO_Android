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
    onFollowButtonClick: (String, Int) -> Unit,
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
        route = MyPageRoute.follow("{memberId}", "{tabNum}"),
        arguments = listOf(
            navArgument("memberId") {
                type = NavType.StringType
            },
            navArgument("tabNum") {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->
        val memberId = navBackStackEntry.arguments?.getString("memberId") ?: ""
        val tabNum = navBackStackEntry.arguments?.getInt("tabNum") ?: 0
        FollowScreen(
            memberId = memberId,
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

    fun follow(memberId: String, tabNum: String) = "$route/follow/$memberId/$tabNum"
    fun profileEdit() = "$route/edit"
    fun bookmark() = "$route/bookmark"
    fun bookmarkPost(postId: String) = "$route/bookmark/$postId"
}