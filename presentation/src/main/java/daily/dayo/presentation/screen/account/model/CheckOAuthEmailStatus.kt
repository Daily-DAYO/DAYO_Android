package daily.dayo.presentation.screen.account.model

enum class CheckOAuthEmailStatus {
    LOADING,
    NORMAL_EMAIL,     // 일반 이메일
    OAUTH_ACCOUNT,    // OAuth 이메일
    ERROR
}