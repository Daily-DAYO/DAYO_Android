package daily.dayo.presentation.screen.write

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import daily.dayo.presentation.viewmodel.WriteViewModel

fun NavController.navigateWrite() {
    navigate(WriteRoute.route)
}

fun NavController.navigatePostEdit(postId: Long) {
    navigate(WriteRoute.postEdit(postId))
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

fun NavController.navigateCrop(index: Int) {
    navigate(WriteRoute.getCropRoute(index))
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.writeNavGraph(
    snackBarHostState: SnackbarHostState,
    navController: NavController,
    navigateToWritePost: (Long) -> Unit,
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
                navController.getBackStackEntry(WriteRoute.writeRouteNavigation)
            }
            WriteRoute(
                postId = null,
                snackBarHostState = snackBarHostState,
                navigateToWritePost = navigateToWritePost,
                onBackClick = onBackClick,
                onTagClick = onTagClick,
                onWriteFolderClick = onWriteFolderClick,
                onCropImageClick = { imgIdx -> navController.navigateCrop(imgIdx) },
                writeViewModel = hiltViewModel(parentStackEntry),
                bottomSheetState = bottomSheetState,
                bottomSheetContent = bottomSheetContent
            )
        }

        composable(
            route = WriteRoute.postEditRoute,
            arguments = listOf(navArgument("postId") { type = NavType.LongType })
        ) { navBackStackEntry ->
            val postId = navBackStackEntry.arguments?.getLong("postId")
            val parentStackEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(WriteRoute.writeRouteNavigation)
            }

            // postId가 null이 아닐 때만 수정 가능
            postId?.let {
                WriteRoute(
                    postId = it,
                    snackBarHostState = snackBarHostState,
                    navigateToWritePost = navigateToWritePost,
                    onBackClick = onBackClick,
                    onTagClick = onTagClick,
                    onWriteFolderClick = onWriteFolderClick,
                    onCropImageClick = { imgIdx -> navController.navigateCrop(imgIdx) },
                    writeViewModel = hiltViewModel(parentStackEntry),
                    bottomSheetState = bottomSheetState,
                    bottomSheetContent = bottomSheetContent
                )
            }
        }

        composable(route = WriteRoute.tag) {
            val parentStackEntry = remember(it) {
                navController.getBackStackEntry(WriteRoute.writeRouteNavigation)
            }
            WriteTagRoute(
                onBackClick = onBackClick,
                writeViewModel = hiltViewModel(parentStackEntry),
            )
        }

        composable(route = WriteRoute.folder) {
            val parentStackEntry = remember(it) {
                navController.getBackStackEntry(WriteRoute.writeRouteNavigation)
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
                navController.getBackStackEntry(WriteRoute.writeRouteNavigation)
            }
            WriteFolderNewRoute(
                onBackClick = onBackClick,
                writeViewModel = hiltViewModel(parentStackEntry),
            )
        }

        composable(
            route = WriteRoute.crop,
            arguments = listOf(navArgument("index") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val parentStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(WriteRoute.writeRouteNavigation)
            }
            val vm: WriteViewModel = hiltViewModel(parentStackEntry)
            val index = backStackEntry.arguments!!.getInt("index")

            ImageCropScreen(
                onBackClick = onBackClick,
                imageIndex = index,
                viewModel = vm,
                navController = navController,
            )
        }
    }
}

object WriteRoute {
    const val route = "write"
    const val postEditRoute = "$route/{postId}"
    const val writeRouteNavigation = "writeNavigation"

    const val tag = "${route}/tag"
    const val folder = "${route}/folder"
    const val folderNew = "${route}/folder/new"

    const val crop = "$route/crop/{index}"
    fun getCropRoute(index: Int): String {
        return crop.replace("{index}", index.toString())
    }

    fun postEdit(postId: Long) = "$route/$postId"
}