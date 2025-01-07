package daily.dayo.presentation.screen.mypage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import daily.dayo.presentation.screen.bookmark.BookmarkScreen
import daily.dayo.presentation.screen.folder.FolderCreateScreen
import daily.dayo.presentation.screen.folder.FolderEditScreen
import daily.dayo.presentation.screen.folder.FolderPostMoveScreen
import daily.dayo.presentation.screen.folder.FolderScreen
import daily.dayo.presentation.screen.write.WriteFolderScreen

fun NavController.navigateProfileEdit() {
    navigate(MyPageRoute.profileEdit())
}

fun NavController.navigateBookmark() {
    navigate(MyPageRoute.bookmark())
}

fun NavController.navigateFolderCreate() {
    navigate(MyPageRoute.folderCreate())
}

fun NavController.navigateFolderEdit(folderId: String) {
    navigate(MyPageRoute.folderEdit(folderId))
}

fun NavController.navigateFolderPostMove() {
    navigate(MyPageRoute.folderPostMove())
}

fun NavGraphBuilder.myPageNavGraph(
    onBackClick: () -> Unit,
    onFollowButtonClick: (String, Int) -> Unit,
    onProfileEditClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onFolderClick: (String) -> Unit,
    onFolderCreateClick: () -> Unit,
    onFolderEditClick: (String) -> Unit,
    onPostMoveClick: () -> Unit
) {
    composable(MyPageRoute.route) {
        MyPageScreen(
            onFollowButtonClick = onFollowButtonClick,
            onProfileEditClick = onProfileEditClick,
            onBookmarkClick = onBookmarkClick,
            onFolderClick = onFolderClick,
            onFolderCreateClick = onFolderCreateClick
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

    composable(MyPageRoute.folderCreate()) {
        FolderCreateScreen(
            onBackClick = onBackClick
        )
    }

    composable(
        route = MyPageRoute.folder("{folderId}"),
        arguments = listOf(
            navArgument("folderId") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val folderId = navBackStackEntry.arguments?.getString("folderId") ?: ""
        FolderScreen(
            folderId = folderId,
            onFolderEditClick = { onFolderEditClick(folderId) },
            onPostMoveClick = onPostMoveClick,
            onBackClick = onBackClick
        )
    }

    composable(
        route = MyPageRoute.folderEdit("{folderId}"),
        arguments = listOf(
            navArgument("folderId") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val folderId = navBackStackEntry.arguments?.getString("folderId") ?: ""
        FolderEditScreen(
            folderId = folderId,
            onBackClick = onBackClick
        )
    }

    composable(route = MyPageRoute.folderPostMove()) {
        FolderPostMoveScreen(
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

    fun folder(folderId: String) = "$route/folder/$folderId"
    fun folderCreate() = "$route/folder/create"
    fun folderEdit(folderId: String) = "$route/folder/edit/$folderId"
    fun folderPostMove() = "$route/folder/move"
}