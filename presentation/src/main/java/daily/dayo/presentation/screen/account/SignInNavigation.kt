package daily.dayo.presentation.screen.account

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope

fun NavController.navigateSignInEmail() {
    this.navigate(SignInRoute.signInEmail)
}

fun NavController.navigateFindPassword() {
    this.navigate(SignInRoute.findPassword)
}

fun NavController.navigateSignUpEmail() {
    this.navigate(SignInRoute.signUpEmail)
}

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.signInNavGraph(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    navController: NavController,
    onBackClick: () -> Unit,
    navigateToSignInEmail: () -> Unit,
    navigateToFindPassword: () -> Unit,
    navigateToSignUpEmail: () -> Unit,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
) {
    composable(route = SignInRoute.route) {
        val parentStackEntry = remember(it) {
            navController.getBackStackEntry(SignInRoute.route)
        }
        SignInRoute(
            accountViewModel = hiltViewModel(parentStackEntry),
            navigateToSignInEmail = navigateToSignInEmail,
        )
    }
    composable(route = SignInRoute.signInEmail) {
        val parentStackEntry = remember(it) {
            navController.getBackStackEntry(SignInRoute.route)
        }
        SignInEmailRoute(
            coroutineScope = coroutineScope,
            snackBarHostState = snackBarHostState,
            onBackClick = onBackClick,
            navigateToFindPassword = navigateToFindPassword,
            navigateToSignUpEmail = navigateToSignUpEmail,
            accountViewModel = hiltViewModel(parentStackEntry),
        )
    }
    composable(route = SignInRoute.findPassword) {
        // TODO FindPasswordRoute
    }

    composable(route = SignInRoute.signUpEmail) {
        val parentStackEntry = remember(it) {
            navController.getBackStackEntry(SignInRoute.route)
        }
        SignUpEmailRoute(
            coroutineScope = coroutineScope,
            snackBarHostState = snackBarHostState,
            onBackClick = onBackClick,
            accountViewModel = hiltViewModel(parentStackEntry),
        )
    }

}

object SignInRoute {
    const val route = "signIn"
    const val signInEmail = "$route/email"
    const val findPassword = "$route/findPassword"
    const val signUpEmail = "$route/signUpEmail"

}