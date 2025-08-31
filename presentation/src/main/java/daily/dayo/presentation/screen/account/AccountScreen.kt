package daily.dayo.presentation.screen.account

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountScreen(
    navigator: AccountNavigator = rememberAccountNavigator()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val bottomSheetState = getBottomSheetDialogState()
    var bottomSheet: (@Composable () -> Unit)? by remember { mutableStateOf(null) }
    val bottomSheetContent: (@Composable () -> Unit) -> Unit = {
        bottomSheet = it
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        Scaffold(
            bottomBar = { bottomSheet?.let { it() } }
        ) {
            Scaffold(
                content = { innerPadding ->
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
                                navigateToRules = { route -> navigator.navigateRules(route) },
                                bottomSheetState = bottomSheetState,
                                bottomSheetContent = bottomSheetContent
                            )
                        }
                    }
                })
        }
    }
}

sealed class AccountScreen(val route: String) {
    object SignIn : AccountScreen(SignInRoute.route)
}