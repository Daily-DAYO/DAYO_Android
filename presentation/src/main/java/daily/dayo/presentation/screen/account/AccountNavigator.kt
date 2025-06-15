package daily.dayo.presentation.screen.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import daily.dayo.presentation.screen.rules.RuleType
import daily.dayo.presentation.screen.rules.navigateRules

class AccountNavigator(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    fun navigateSignIn() {
        navController.navigateSignIn()
    }

    fun navigateSignInEmail() {
        navController.navigateSignInEmail()
    }

    fun navigateResetPassword() {
        navController.navigateResetPassword()
    }

    fun navigateSignUpEmail() {
        navController.navigateSignUpEmail()
    }

    fun navigateProfileSetting() {
        navController.navigateProfileSetting()
    }

    fun navigateRules(ruleType: RuleType) {
        navController.navigateRules(ruleType)
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}

@Composable
internal fun rememberAccountNavigator(
    navController: NavHostController = rememberNavController(),
): AccountNavigator = remember(navController) {
    AccountNavigator(navController)
}