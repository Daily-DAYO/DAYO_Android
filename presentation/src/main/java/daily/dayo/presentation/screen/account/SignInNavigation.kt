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
import daily.dayo.presentation.screen.rules.RuleRoute
import daily.dayo.presentation.screen.rules.RuleType
import kotlinx.coroutines.CoroutineScope

fun NavController.navigateSignInEmail() {
    this.navigate(SignInRoute.signInEmail)
}

fun NavController.navigateResetPassword() {
    this.navigate(SignInRoute.resetPassword)
}

fun NavController.navigateSignUpEmail() {
    this.navigate(SignInRoute.signUpEmail)
}

fun NavController.navigateProfileSetting() {
    this.navigate(SignInRoute.profileSetting)
}

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.signInNavGraph(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    navController: NavController,
    onBackClick: () -> Unit,
    navigateToSignInEmail: () -> Unit,
    navigateToResetPassword: () -> Unit,
    navigateToSignUpEmail: () -> Unit,
    navigateToRules: (RuleType) -> Unit,
    navigateToProfileSetting: () -> Unit,
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
            navigateToRules = navigateToRules,
            navigateToProfileSetting = navigateToProfileSetting,
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
            navigateToFindPassword = navigateToResetPassword,
            navigateToSignUpEmail = navigateToSignUpEmail,
            accountViewModel = hiltViewModel(parentStackEntry),
        )
    }

    composable(route = SignInRoute.resetPassword) {
        val parentStackEntry = remember(it) {
            navController.getBackStackEntry(SignInRoute.route)
        }
        ResetPasswordRoute(
            coroutineScope = coroutineScope,
            snackBarHostState = snackBarHostState,
            onBackClick = onBackClick,
            accountViewModel = hiltViewModel(parentStackEntry),
        )
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
            profileSettingViewModel = hiltViewModel(parentStackEntry),
        )
    }

    composable(route = SignInRoute.profileSetting) {
        val parentStackEntry = remember(it) {
            navController.getBackStackEntry(SignInRoute.route)
        }
        SignUpEmailRoute(
            coroutineScope = coroutineScope,
            snackBarHostState = snackBarHostState,
            onBackClick = onBackClick,
            accountViewModel = hiltViewModel(parentStackEntry),
            profileSettingViewModel = hiltViewModel(parentStackEntry),
            startSignUpStep = SignUpStep.PROFILE_SETUP
        )
    }

    composable(route = RuleRoute.privacyPolicy) {
        RuleRoute(
            onBackClick = onBackClick,
            ruleType = RuleType.PRIVACY_POLICY
        )
    }

    composable(route = RuleRoute.termsAndConditions) {
        RuleRoute(
            onBackClick = onBackClick,
            ruleType = RuleType.TERMS_AND_CONDITIONS
        )
    }

}

object SignInRoute {
    const val route = "signIn"
    const val signInEmail = "$route/email"
    const val resetPassword = "$route/resetPassword"
    const val signUpEmail = "$route/signUpEmail"
    const val profileSetting = "$route/profileSetting"
}