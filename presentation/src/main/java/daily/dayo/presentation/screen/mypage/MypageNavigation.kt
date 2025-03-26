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

fun NavController.navigateMyPage() {
    navigate(MyPageRoute.route) {
        launchSingleTop = true
    }
}

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

fun NavController.navigateFolderPostMove(folderId: String) {
    navigate(MyPageRoute.folderPostMove(folderId))
}

fun NavController.navigateFollowMenu(memberId: String, tabNum: Int) {
    navigate(MyPageRoute.follow(memberId, "$tabNum"))
}

fun NavController.navigateFolder(folderId: String) {
    navigate(MyPageRoute.folder(folderId))
}

fun NavController.navigateBackToFolder(folderId: String) {
    navigate(MyPageRoute.folder(folderId)) {
        popUpTo(MyPageRoute.folder(folderId)) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.myPageNavGraph(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFollowButtonClick: (String, Int) -> Unit,
    onProfileClick: (String) -> Unit,
    onProfileEditClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onFolderClick: (String) -> Unit,
    onFolderCreateClick: () -> Unit,
    onFolderEditClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onPostMoveClick: (String) -> Unit,
    navigateBackToFolder: (String) -> Unit
) {
    composable(MyPageRoute.route) {
        MyPageScreen(
            onSettingsClick = onSettingsClick,
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
            onProfileClick = onProfileClick,
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
            onPostClick = onPostClick,
            onFolderEditClick = { onFolderEditClick(folderId) },
            onPostMoveClick = { onPostMoveClick(folderId) },
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

    composable(
        route = MyPageRoute.folderPostMove("{folderId}"),
        arguments = listOf(
            navArgument("folderId") {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val folderId = navBackStackEntry.arguments?.getString("folderId") ?: ""
        FolderPostMoveScreen(
            navigateBackToFolder = { navigateBackToFolder(folderId) },
            navigateToCreateNewFolder = onFolderCreateClick,
            onBackClick = onBackClick
        )
    }
}

object MyPageRoute {
    const val route = "myPage"

    // profile edit
    fun profileEdit() = "$route/edit"

    // follow
    fun follow(memberId: String, tabNum: String) = "$route/follow/$memberId/$tabNum"

    // bookmark
    fun bookmark() = "$route/bookmark"

    // folder
    fun folder(folderId: String) = "$route/folder/$folderId"
    fun folderCreate() = "$route/folder/create"
    fun folderEdit(folderId: String) = "$route/folder/edit/$folderId"
    fun folderPostMove(folderId: String) = "$route/folder/move/$folderId"
}
