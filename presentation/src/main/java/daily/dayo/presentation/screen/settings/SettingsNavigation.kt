package daily.dayo.presentation.screen.settings

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import daily.dayo.presentation.screen.rules.RuleRoute
import daily.dayo.presentation.screen.rules.RuleType
import kotlinx.coroutines.CoroutineScope

fun NavController.navigateSettings() {
    navigate(SettingsRoute.route)
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

fun NavGraphBuilder.settingsNavGraph(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    onProfileEditClick: () -> Unit,
    onBlockUsersClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingNotificationClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onNoticesClick: () -> Unit,
    onInformationClick: () -> Unit,
    onRulesClick: (RuleType) -> Unit,
) {
    composable(SettingsRoute.route) {
        SettingsScreen(
            onProfileEditClick = onProfileEditClick,
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
}

object SettingsRoute {
    const val route = "settings"

    const val changePassword = "${route}/changePassword"
    const val notification = "${route}/notification"
    const val information = "${route}/information"
}