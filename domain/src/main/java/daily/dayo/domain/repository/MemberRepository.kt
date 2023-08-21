package daily.dayo.domain.repository

import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Profile
import daily.dayo.domain.model.User
import daily.dayo.domain.model.UserTokens
import daily.dayo.domain.model.UsersBlocked
import okhttp3.MultipartBody

interface MemberRepository {

    suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part?
    ): NetworkResponse<String>

    suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?,
        onBasicProfileImg: Boolean
    ): NetworkResponse<Void>

    suspend fun requestLoginKakao(accessToken: String): NetworkResponse<UserTokens>
    suspend fun requestLoginEmail(email: String, password: String): NetworkResponse<UserTokens>
    suspend fun requestMemberInfo(): NetworkResponse<User>
    suspend fun requestCheckEmailDuplicate(email: String): NetworkResponse<Void>
    suspend fun requestCheckNicknameDuplicate(nickname: String): NetworkResponse<Void>
    suspend fun requestCertificateEmail(email: String): NetworkResponse<String>
    suspend fun requestRefreshToken(): NetworkResponse<String>
    suspend fun requestDeviceToken(deviceToken: String?): NetworkResponse<Void>
    suspend fun requestMyProfile(): NetworkResponse<Profile>
    suspend fun requestOtherProfile(memberId: String): NetworkResponse<Profile>
    suspend fun requestResign(content: String): NetworkResponse<Void>
    suspend fun requestReceiveAlarm(): NetworkResponse<Boolean>
    suspend fun requestChangeReceiveAlarm(onReceiveAlarm: Boolean): NetworkResponse<Void>
    suspend fun requestLogout(): NetworkResponse<Void>
    suspend fun requestCheckEmail(email: String): NetworkResponse<Void>
    suspend fun requestCheckEmailAuth(email: String): NetworkResponse<String>
    suspend fun requestCheckCurrentPassword(password: String): NetworkResponse<Void>
    suspend fun requestChangePassword(email: String, password: String): NetworkResponse<Void>
    suspend fun requestSettingChangePassword(email: String, password: String): NetworkResponse<Void>
    suspend fun requestBlockList(): NetworkResponse<UsersBlocked>
}