package daily.dayo.presentation.screen.mypage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import daily.dayo.presentation.screen.bookmark.BookmarkScreen

fun NavController.navigateBookmark() {
    navigate(MyPageRoute.bookmark())
}

fun NavGraphBuilder.myPageNavGraph(
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    composable(MyPageRoute.route) {
        MyPageScreen(
            onBookmarkClick = onBookmarkClick
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

    fun bookmark() = "$route/bookmark"
    fun bookmarkPost(postId: String) = "$route/bookmark/$postId"
}