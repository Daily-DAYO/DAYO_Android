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

fun NavController.navigateFolderEdit(folderId: Long) {
    navigate(MyPageRoute.folderEdit(folderId))
}

fun NavController.navigateFolderPostMove(folderId: Long) {
    navigate(MyPageRoute.folderPostMove(folderId))
}

fun NavController.navigateFollowMenu(memberId: String, tabNum: Int) {
    navigate(MyPageRoute.follow(memberId, "$tabNum"))
}

fun NavController.navigateFolder(folderId: Long) {
    navigate(MyPageRoute.folder(folderId))
}

fun NavController.navigateBackToFolder(folderId: Long) {
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
    onFolderClick: (Long) -> Unit,
    onFolderCreateClick: () -> Unit,
    onFolderEditClick: (Long) -> Unit,
    onPostClick: (Long) -> Unit,
    onPostMoveClick: (Long) -> Unit,
    navigateBackToFolder: (Long) -> Unit
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
        route = MyPageRoute.folderRoute,
        arguments = listOf(
            navArgument("folderId") {
                type = NavType.LongType
            }
        )
    ) { navBackStackEntry ->
        navBackStackEntry.arguments?.getLong("folderId")?.let { folderId ->
            FolderScreen(
                folderId = folderId,
                onPostClick = onPostClick,
                onFolderEditClick = { onFolderEditClick(folderId) },
                onPostMoveClick = { onPostMoveClick(folderId) },
                onBackClick = onBackClick
            )
        }
    }

    composable(
        route = MyPageRoute.folderEditRoute,
        arguments = listOf(
            navArgument("folderId") {
                type = NavType.LongType
            }
        )
    ) { navBackStackEntry ->
        navBackStackEntry.arguments?.getLong("folderId")?.let { folderId ->
            FolderEditScreen(
                folderId = folderId,
                onBackClick = onBackClick
            )
        }
    }

    composable(
        route = MyPageRoute.folderPostMoveRoute,
        arguments = listOf(
            navArgument("folderId") {
                type = NavType.LongType
            }
        )
    ) { navBackStackEntry ->
        navBackStackEntry.arguments?.getLong("folderId")?.let { folderId ->
            FolderPostMoveScreen(
                navigateBackToFolder = { navigateBackToFolder(folderId) },
                navigateToCreateNewFolder = onFolderCreateClick,
                onBackClick = onBackClick
            )
        }
    }
}

object MyPageRoute {
    const val route = "myPage"
    const val folderRoute = "$route/folder/{folderId}"
    const val folderEditRoute = "$route/folder/edit/{folderId}"
    const val folderPostMoveRoute = "$route/folder/move/{folderId}"

    // profile edit
    fun profileEdit() = "$route/edit"

    // follow
    fun follow(memberId: String, tabNum: String) = "$route/follow/$memberId/$tabNum"

    // bookmark
    fun bookmark() = "$route/bookmark"

    // folder
    fun folder(folderId: Long) = "$route/folder/$folderId"
    fun folderCreate() = "$route/folder/create"
    fun folderEdit(folderId: Long) = "$route/folder/edit/$folderId"
    fun folderPostMove(folderId: Long) = "$route/folder/move/$folderId"
}
