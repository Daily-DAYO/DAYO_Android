package daily.dayo.presentation.screen.rules

import androidx.navigation.NavController

enum class RuleType(val koreanName: String, val fileName: String) {
    PRIVACY_POLICY("개인정보 처리방침", "privacy"),
    TERMS_AND_CONDITIONS("이용약관", "terms")
}

fun NavController.navigateRules(ruleType: RuleType) {
    when (ruleType) {
        RuleType.PRIVACY_POLICY -> this.navigate(RuleRoute.privacyPolicy)
        RuleType.TERMS_AND_CONDITIONS -> this.navigate(RuleRoute.termsAndConditions)
    }
}

object RuleRoute {
    const val route = "rule"
    const val privacyPolicy = "${route}/privacy_policy"
    const val termsAndConditions = "${route}/terms_and_conditions"
}