package daily.dayo.presentation.screen.account.model

enum class SignUpStep(val stepNum: Int) {
    EMAIL_INPUT(1),           // 이메일 주소 입력
    EMAIL_VERIFICATION(2),    // 인증번호 입력
    PASSWORD_INPUT(3),        // 비밀번호 입력
    PASSWORD_CONFIRM(4),      // 비밀번호 재입력
    PROFILE_SETUP(5),         // 프로필 설정 (사진 및 닉네임)
    SIGNUP_COMPLETE(6)        // 회원가입 완료
}