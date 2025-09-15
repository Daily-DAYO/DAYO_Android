package daily.dayo.presentation.screen.account.model

enum class NicknameCertificationState {
    BEFORE_CERTIFICATION, // 확인 전
    IN_PROGRESS_CHECK_NICKNAME, // 닉네임 중복 확인 중
    SUCCESS,              // 중복되지 않은 닉네임
    DUPLICATE_NICKNAME,   // 중복된 닉네임
    INVALID_FORMAT        // 잘못된 형식의 닉네임
}