package daily.dayo.presentation.screen.write

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavController.navigateWrite() {
    navigate(WriteRoute.route)
}

fun NavController.navigateWriteTag() {
    navigate(WriteRoute.tag)
}

fun NavController.navigateWriteFolder() {
    navigate(WriteRoute.folder)
}

fun NavController.navigateWriteFolderNew() {
    navigate(WriteRoute.folderNew)
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.writeNavGraph(
    snackBarHostState: SnackbarHostState,
    navController: NavController,
    onBackClick: () -> Unit,
    onTagClick: () -> Unit,
    onWriteFolderClick: () -> Unit,
    onWriteFolderNewClick: () -> Unit,
    onAdRequest: (onRewardSuccess: () -> Unit) -> Unit,
    bottomSheetState: BottomSheetScaffoldState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
) {
    navigation(
        startDestination = WriteRoute.route,
        route = "writeNavigation"
    ) {
        composable(route = WriteRoute.route) {
            val parentStackEntry = remember(it) {
                navController.getBackStackEntry(WriteRoute.route)
            }
            WriteRoute(
                snackBarHostState = snackBarHostState,
                onBackClick = onBackClick,
                onTagClick = onTagClick,
                onWriteFolderClick = onWriteFolderClick,
                writeViewModel = hiltViewModel(parentStackEntry),
                bottomSheetState = bottomSheetState,
                bottomSheetContent = bottomSheetContent
            )
        }

        composable(route = WriteRoute.tag) {
            val parentStackEntry = remember(it) {
                navController.getBackStackEntry(WriteRoute.route)
            }
            WriteTagRoute(
                onBackClick = onBackClick,
                writeViewModel = hiltViewModel(parentStackEntry),
            )
        }

        composable(route = WriteRoute.folder) {
            val parentStackEntry = remember(it) {
                navController.getBackStackEntry(WriteRoute.route)
            }
            WriteFolderRoute(
                onBackClick = onBackClick,
                onWriteFolderNewClick = onWriteFolderNewClick,
                onAdRequest = onAdRequest,
                writeViewModel = hiltViewModel(parentStackEntry),
                profileViewModel = hiltViewModel(parentStackEntry),
                accountViewModel = hiltViewModel(parentStackEntry),
            )
        }

        composable(route = WriteRoute.folderNew) {
            val parentStackEntry = remember(it) {
                navController.getBackStackEntry(WriteRoute.route)
            }
            WriteFolderNewRoute(
                onBackClick = onBackClick,
                writeViewModel = hiltViewModel(parentStackEntry),
            )
        }
    }
}

object WriteRoute {
    const val route = "write"

    const val tag = "${route}/tag"
    const val folder = "${route}/folder"
    const val folderNew = "${route}/folder/new"
}