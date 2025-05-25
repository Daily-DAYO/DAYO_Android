package daily.dayo.presentation.screen.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope
import daily.dayo.presentation.screen.account.WithdrawScreen

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

fun NavGraphBuilder.settingsNavGraph(
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    onProfileEditClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onBackClick: () -> Unit,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
    onBlockUsersClick: () -> Unit,
    onSettingNotificationClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onNoticesClick: () -> Unit,
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

    composable(SettingsRoute.withdraw) {
        WithdrawScreen(
            onBackClick = onBackClick,
            snackBarHostState = snackBarHostState,
            bottomSheetState = bottomSheetState,
            bottomSheetContent = bottomSheetContent
        )
    }
}

object SettingsRoute {
    const val route = "settings"

    const val withdraw = "${route}/withdraw"
    const val changePassword = "${route}/changePassword"
    const val notification = "${route}/notification"
}