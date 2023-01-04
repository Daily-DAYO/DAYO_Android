package com.daily.dayo.data.repository

import com.daily.dayo.common.Resource
import com.daily.dayo.common.setExceptionHandling
import com.daily.dayo.data.datasource.remote.firebase.FirebaseMessagingService
import com.daily.dayo.data.datasource.remote.member.*
import com.daily.dayo.domain.repository.MemberRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberApiService: MemberApiService
) : MemberRepository {

    override suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part
    ): Resource<MemberSignupResponse> =
        setExceptionHandling {
            memberApiService.requestSignupEmail(
                email,
                nickname,
                password,
                profileImg
            )
        }

    override suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?,
        onBasicProfileImg: Boolean
    ): Resource<Void> =
        setExceptionHandling { memberApiService.requestUpdateMyProfile(
            nickname,
            profileImg,
            onBasicProfileImg
        ) }

    override suspend fun requestLoginKakao(body: MemberOAuthRequest): Resource<MemberOAuthResponse> =
        setExceptionHandling { memberApiService.requestLoginKakao(body) }

    override suspend fun requestLoginEmail(body: MemberSignInRequest): Resource<MemberSignInResponse> =
        setExceptionHandling { memberApiService.requestLoginEmail(body) }

    override suspend fun requestMemberInfo(): Resource<MemberInfoResponse> =
        setExceptionHandling { memberApiService.requestMemberInfo() }

    override suspend fun requestCheckEmailDuplicate(email: String): Resource<Void> =
        setExceptionHandling { memberApiService.requestCheckEmailDuplicate(email) }

    override suspend fun requestCertificateEmail(email: String): Resource<MemberAuthCodeResponse> =
        setExceptionHandling { memberApiService.requestCertificateEmail(email) }

    override suspend fun requestRefreshToken(): Resource<RefreshTokenResponse> =
        setExceptionHandling { memberApiService.requestRefreshToken() }

    override suspend fun requestDeviceToken(body: DeviceTokenRequest): Resource<Void> =
        setExceptionHandling { memberApiService.requestDeviceToken(body) }

    override suspend fun requestMyProfile(): Resource<MemberMyProfileResponse> =
        setExceptionHandling { memberApiService.requestMyProfile() }

    override suspend fun requestOtherProfile(memberId: String): Resource<MemberOtherProfileResponse> =
        setExceptionHandling { memberApiService.requestOtherProfile(memberId) }

    override suspend fun requestResign(content: String): Resource<Void> =
        setExceptionHandling { memberApiService.requestResign(content) }

    override suspend fun requestReceiveAlarm(): Resource<ReceiveAlarmResponse> =
        setExceptionHandling { memberApiService.requestReceiveAlarm() }

    override suspend fun requestChangeReceiveAlarm(body: ChangeReceiveAlarmRequest): Resource<Void> =
        setExceptionHandling { memberApiService.requestChangeReceiveAlarm(body) }

    override suspend fun requestLogout(): Resource<Void> =
        setExceptionHandling { memberApiService.requestLogout() }

    override suspend fun requestCheckEmail(email: String): Resource<Void> =
        setExceptionHandling { memberApiService.requestCheckEmail(email) }

    override suspend fun requestCheckEmailAuth(email: String): Resource<MemberAuthCodeResponse> =
        setExceptionHandling { memberApiService.requestCheckEmailAuth(email) }

    override suspend fun requestCheckCurrentPassword(body: CheckPasswordRequest): Resource<Void> =
        setExceptionHandling { memberApiService.requestCheckCurrentPassword(body) }

    override suspend fun requestChangePassword(body: ChangePasswordRequest): Resource<Void> =
        setExceptionHandling { memberApiService.requestChangePassword(body) }

    override suspend fun requestSettingChangePassword(body: ChangePasswordRequest): Resource<Void> =
        setExceptionHandling { memberApiService.requestSettingChangePassword(body) }

    // Firebase Messaging Service
    override suspend fun getCurrentFcmToken(): String =
        FirebaseMessagingService().getCurrentToken()

    override suspend fun registerFcmToken() =
        FirebaseMessagingService().registerFcmToken()

    override fun unregisterFcmToken() =
        FirebaseMessagingService().unregisterFcmToken()
}