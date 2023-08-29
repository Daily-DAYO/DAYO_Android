package daily.dayo.data.repository

import android.content.Context
import daily.dayo.data.datasource.local.SharedManager
import daily.dayo.data.datasource.remote.member.*
import daily.dayo.data.mapper.toProfile
import daily.dayo.data.mapper.toUserTokenResponse
import daily.dayo.data.mapper.toUsersBlocked
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Profile
import daily.dayo.domain.model.User
import daily.dayo.domain.model.UserTokens
import daily.dayo.domain.model.UsersBlocked
import daily.dayo.domain.repository.MemberRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberApiService: MemberApiService,
    private val context: Context
) : MemberRepository {

    override suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part?
    ): NetworkResponse<String> =
        when (val response = memberApiService.requestSignupEmail(
            email,
            nickname,
            password,
            profileImg
        )) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.memberId)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?,
        onBasicProfileImg: Boolean
    ): NetworkResponse<Void> =
        memberApiService.requestUpdateMyProfile(nickname, profileImg, onBasicProfileImg)

    override suspend fun requestLoginKakao(accessToken: String): NetworkResponse<UserTokens> =
        when (val response = memberApiService.requestLoginKakao(MemberOAuthRequest(accessToken))) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toUserTokenResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestLoginEmail(
        email: String,
        password: String
    ): NetworkResponse<UserTokens> =
        when (val response = memberApiService.requestLoginEmail(
            MemberSignInRequest(
                email = email,
                password = password
            )
        )) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toUserTokenResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestMemberInfo(): NetworkResponse<User> =
        when (val response = memberApiService.requestMemberInfo()) {
            is NetworkResponse.Success -> NetworkResponse.Success(
                response.body?.let { info ->
                    User(
                        email = info.email,
                        memberId = info.memberId,
                        nickname = info.nickname,
                        profileImg = info.profileImg
                    )
                }
            )

            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }


    override suspend fun requestCheckEmailDuplicate(email: String): NetworkResponse<Void> =
        memberApiService.requestCheckEmailDuplicate(email)

    override suspend fun requestCheckNicknameDuplicate(nickname: String): NetworkResponse<Void> =
        memberApiService.requestCheckNicknameDuplicate(nickname)

    override suspend fun requestCertificateEmail(email: String): NetworkResponse<String> =
        when (val response = memberApiService.requestCertificateEmail(email)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.authCode)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }


    override suspend fun requestRefreshToken(): NetworkResponse<String> =
        when (val response = memberApiService.requestRefreshToken()) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.accessToken)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDeviceToken(deviceToken: String?): NetworkResponse<Void> =
        memberApiService.requestDeviceToken(DeviceTokenRequest(deviceToken = deviceToken))

    override suspend fun requestMyProfile(): NetworkResponse<Profile> =
        when (val response = memberApiService.requestMyProfile()) {
            is NetworkResponse.Success -> NetworkResponse.Success(
                response.body?.toProfile(
                    requestCurrentUserInfo().memberId ?: ""
                )
            )

            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestOtherProfile(memberId: String): NetworkResponse<Profile> =
        when (val response = memberApiService.requestOtherProfile(memberId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toProfile())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestResign(content: String): NetworkResponse<Void> =
        memberApiService.requestResign(content)

    override suspend fun requestReceiveAlarm(): NetworkResponse<Boolean> =
        when (val response = memberApiService.requestReceiveAlarm()) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.onReceiveAlarm)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestChangeReceiveAlarm(onReceiveAlarm: Boolean): NetworkResponse<Void> =
        memberApiService.requestChangeReceiveAlarm(ChangeReceiveAlarmRequest(onReceiveAlarm))

    override suspend fun requestLogout(): NetworkResponse<Void> =
        memberApiService.requestLogout()

    override suspend fun requestCheckEmail(email: String): NetworkResponse<Void> =
        memberApiService.requestCheckEmail(email)

    override suspend fun requestCheckEmailAuth(email: String): NetworkResponse<String> =
        when (val response = memberApiService.requestCheckEmailAuth(email)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.authCode)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestCheckCurrentPassword(password: String): NetworkResponse<Void> =
        memberApiService.requestCheckCurrentPassword(CheckPasswordRequest(password = password))

    override suspend fun requestChangePassword(
        email: String,
        password: String
    ): NetworkResponse<Void> =
        memberApiService.requestChangePassword(
            ChangePasswordRequest(
                email = email,
                password = password
            )
        )

    override suspend fun requestSettingChangePassword(
        email: String,
        password: String
    ): NetworkResponse<Void> =
        memberApiService.requestSettingChangePassword(
            ChangePasswordRequest(
                email = email,
                password = password
            )
        )

    override suspend fun requestBlockList(): NetworkResponse<UsersBlocked> =
        when (val response = memberApiService.requestBlockList()) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toUsersBlocked())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override fun clearClearCurrentUser() {
        SharedManager(context).clearPreferences()
    }

    override fun requestCurrentUserInfo(): User =
        SharedManager(context).getCurrentUser()

    override fun saveCurrentUserInfo(userInfo: Any?) {
        SharedManager(context).saveCurrentUser(userInfo = userInfo)
    }

    override fun saveCurrentUserAccessToken(accessToken: String) {
        SharedManager(context).setAccessToken(accessToken = accessToken)
    }

    override fun requestCurrentUserNotiDevicePermit(): Boolean =
        SharedManager(context).notiDevicePermit

    override fun requestCurrentUserNotiNoticePermit(): Boolean =
        SharedManager(context).notiNoticePermit

    override fun requestCurrentUserNotiDevicePermit(isPermit: Boolean) {
        SharedManager(context).notiDevicePermit = isPermit
    }

    override fun requestCurrentUserNotiNoticePermit(isPermit: Boolean) {
        SharedManager(context).notiNoticePermit = isPermit
    }
}