package daily.dayo.presentation.screen.account.model

enum class EmailCertificationState {
    BEFORE_CERTIFICATION, // 인증 전
    IN_PROGRESS_CHECK_EMAIL, // 이메일 중복/형식 확인 중
    SUCCESS_CHECK_EMAIL,              // 인증 성공
    NOT_EXIST_EMAIL,      // 존재하지 않는 이메일로 인증 실패
    DUPLICATE_EMAIL,      // 중복된 이메일로 인증 실패
    INVALID_FORMAT,       // 잘못된 형식의 이메일로 인증 실패
    OAUTH_EMAIL,          // 소셜계정으로 가입한 이메일로 인증 실패
    ERROR,                // 인증 중 오류 발생
}