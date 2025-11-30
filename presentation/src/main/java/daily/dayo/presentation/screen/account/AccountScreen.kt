package daily.dayo.presentation.screen.account

import BottomSheetController
import LocalBottomSheetController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountScreen(
    navigator: AccountNavigator = rememberAccountNavigator()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val bottomSheetController = remember { BottomSheetController() }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CompositionLocalProvider(LocalBottomSheetController provides bottomSheetController) {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navigator.navController,
                    startDestination = AccountScreen.SignIn.route
                ) {
                    signInNavGraph(
                        coroutineScope = coroutineScope,
                        snackBarHostState = snackBarHostState,
                        navController = navigator.navController,
                        onBackClick = { navigator.popBackStack() },
                        navigateToSignIn = { navigator.navigateSignIn() },
                        navigateToSignInEmail = { navigator.navigateSignInEmail() },
                        navigateToResetPassword = { navigator.navigateResetPassword() },
                        navigateToSignUpEmail = { navigator.navigateSignUpEmail() },
                        navigateToProfileSetting = { navigator.navigateProfileSetting() },
                        navigateToRules = { route -> navigator.navigateRules(route) }
                    )
                }

                if (bottomSheetController.isVisible) {
                    ModalBottomSheet(
                        onDismissRequest = { bottomSheetController.hide() },
                        modifier = Modifier.navigationBarsPadding(),
                        sheetState = bottomSheetState,
                        dragHandle = null
                    ) {
                        bottomSheetController.sheetContent()
                    }
                }
            }
        }
    }
}

sealed class AccountScreen(val route: String) {
    object SignIn : AccountScreen(SignInRoute.route)
}