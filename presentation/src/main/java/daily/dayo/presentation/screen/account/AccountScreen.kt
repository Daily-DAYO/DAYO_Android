package daily.dayo.presentation.screen.account

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountScreen(
    navigator: AccountNavigator = rememberAccountNavigator()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var bottomSheetContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    val bottomSheetState = getBottomSheetDialogState()
    val bottomSheetDimAlpha by remember {
        derivedStateOf { if (bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded) 0.6f else 0f }
    }
    val animatedDimAlpha by animateFloatAsState(targetValue = bottomSheetDimAlpha)

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetDragHandle = null,
        sheetContent = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                bottomSheetContent?.invoke()
            }
        },
        sheetPeekHeight = 0.dp,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        },
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
                        bottomSheetContent = { content ->
                            bottomSheetContent = content
                        }
                    )
                }

                if (animatedDimAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Dark.copy(alpha = animatedDimAlpha))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                coroutineScope.launch { bottomSheetState.bottomSheetState.hide() }
                            }
                    )
                }
            }
        }
    )
}

sealed class AccountScreen(val route: String) {
    object SignIn : AccountScreen(SignInRoute.route)
}