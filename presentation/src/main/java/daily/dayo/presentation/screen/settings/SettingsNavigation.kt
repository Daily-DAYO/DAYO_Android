package daily.dayo.presentation.screen.settings

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import daily.dayo.presentation.screen.account.WithdrawScreen
import daily.dayo.presentation.screen.rules.RuleRoute
import daily.dayo.presentation.screen.rules.RuleType
import kotlinx.coroutines.CoroutineScope

fun NavController.navigateSettings() {
    navigate(SettingsRoute.route)
}

fun NavController.navigateWithdraw() {
    navigate(SettingsRoute.withdraw)
}

fun NavController.navigateChangePassword() {
    navigate(SettingsRoute.changePassword)
}

fun NavController.navigateSettingsNotification() {
    navigate(SettingsRoute.notification)
}

fun NavController.navigateInformation() {
    navigate(SettingsRoute.information)
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.settingsNavGraph(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    onProfileEditClick: () -> Unit,
    onBlockUsersClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onBackClick: () -> Unit,
    bottomSheetState: BottomSheetScaffoldState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
    onSettingNotificationClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onNoticesClick: () -> Unit,
    onInformationClick: () -> Unit,
    onRulesClick: (RuleType) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
) {
    composable(SettingsRoute.route) {
        SettingsScreen(
            onProfileEditClick = onProfileEditClick,
            onWithdrawClick = onWithdrawClick,
            onBackClick = onBackClick,
            onPasswordChangeClick = onPasswordChangeClick,
            onBlockUsersClick = onBlockUsersClick,
            onSettingNotificationClick = onSettingNotificationClick,
            onNoticesClick = onNoticesClick,
            onInformationClick = onInformationClick
        )
    }

    composable(route = SettingsRoute.notification) {
        SettingNotificationScreen(
            onBackClick = onBackClick,
        )
    }

    composable(SettingsRoute.changePassword) {
        ChangePasswordScreen(
            coroutineScope = coroutineScope,
            snackBarHostState = snackBarHostState,
            onBackClick = onBackClick,
        )
    }

    composable(SettingsRoute.information) {
        AppInformationScreen(
            onBackClick = onBackClick,
            onRulesClick = onRulesClick,
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

    composable(SettingsRoute.withdraw) {
        WithdrawScreen(
            onBackClick = onBackClick,
            bottomSheetState = bottomSheetState,
            bottomSheetContent = bottomSheetContent,
            snackBarHostState = snackBarHostState,
            onNavigateToHome = onNavigateToHome,
            onNavigateToMyPage = onNavigateToMyPage
        )
    }
}

object SettingsRoute {
    const val route = "settings"

    const val withdraw = "${route}/withdraw"
    const val changePassword = "${route}/changePassword"
    const val notification = "${route}/notification"
    const val information = "${route}/information"
}