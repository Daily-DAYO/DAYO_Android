package daily.dayo.presentation.screen.account.model

enum class EmailExistenceStatus {
    IDLE,        // 처음 진입한 상태, 아직 아무 입력도 없음
    LOADING,     // 이메일 존재 여부 확인 중
    EXISTS,      // 이메일 존재
    NOT_EXISTS,  // 이메일 존재하지 않음
    ERROR        // 네트워크 오류, 예외 등 실패 상황
}