package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.firebase.FirebaseMessagingService
import com.daily.dayo.data.datasource.remote.member.*
import com.daily.dayo.domain.model.NetworkResponse
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
        profileImg: MultipartBody.Part?
    ): NetworkResponse<MemberSignupResponse> =
        memberApiService.requestSignupEmail(
            email,
            nickname,
            password,
            profileImg
        )

    override suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?,
        onBasicProfileImg: Boolean
    ): NetworkResponse<Void> =
        memberApiService.requestUpdateMyProfile(nickname, profileImg, onBasicProfileImg)

    override suspend fun requestLoginKakao(body: MemberOAuthRequest): NetworkResponse<MemberOAuthResponse> =
        memberApiService.requestLoginKakao(body)

    override suspend fun requestLoginEmail(body: MemberSignInRequest): NetworkResponse<MemberSignInResponse> =
        memberApiService.requestLoginEmail(body)

    override suspend fun requestMemberInfo(): NetworkResponse<MemberInfoResponse> =
        memberApiService.requestMemberInfo()

    override suspend fun requestCheckEmailDuplicate(email: String): NetworkResponse<Void> =
        memberApiService.requestCheckEmailDuplicate(email)

    override suspend fun requestCertificateEmail(email: String): NetworkResponse<MemberAuthCodeResponse> =
        memberApiService.requestCertificateEmail(email)

    override suspend fun requestRefreshToken(): NetworkResponse<RefreshTokenResponse> =
        memberApiService.requestRefreshToken()

    override suspend fun requestDeviceToken(body: DeviceTokenRequest): NetworkResponse<Void> =
        memberApiService.requestDeviceToken(body)

    override suspend fun requestMyProfile(): NetworkResponse<MemberMyProfileResponse> =
        memberApiService.requestMyProfile()

    override suspend fun requestOtherProfile(memberId: String): NetworkResponse<MemberOtherProfileResponse> =
        memberApiService.requestOtherProfile(memberId)

    override suspend fun requestResign(content: String): NetworkResponse<Void> =
        memberApiService.requestResign(content)

    override suspend fun requestReceiveAlarm(): NetworkResponse<ReceiveAlarmResponse> =
        memberApiService.requestReceiveAlarm()

    override suspend fun requestChangeReceiveAlarm(body: ChangeReceiveAlarmRequest): NetworkResponse<Void> =
        memberApiService.requestChangeReceiveAlarm(body)

    override suspend fun requestLogout(): NetworkResponse<Void> =
        memberApiService.requestLogout()

    override suspend fun requestCheckEmail(email: String): NetworkResponse<Void> =
        memberApiService.requestCheckEmail(email)

    override suspend fun requestCheckEmailAuth(email: String): NetworkResponse<MemberAuthCodeResponse> =
        memberApiService.requestCheckEmailAuth(email)

    override suspend fun requestCheckCurrentPassword(body: CheckPasswordRequest): NetworkResponse<Void> =
        memberApiService.requestCheckCurrentPassword(body)

    override suspend fun requestChangePassword(body: ChangePasswordRequest): NetworkResponse<Void> =
        memberApiService.requestChangePassword(body)

    override suspend fun requestSettingChangePassword(body: ChangePasswordRequest): NetworkResponse<Void> =
        memberApiService.requestSettingChangePassword(body)

    override suspend fun requestBlockList(): NetworkResponse<MemberBlockResponse> =
        memberApiService.requestBlockList()

    // Firebase Messaging Service
    override suspend fun getCurrentFcmToken(): String =
        FirebaseMessagingService().getCurrentToken()

    override suspend fun registerFcmToken() =
        FirebaseMessagingService().registerFcmToken()

    override fun unregisterFcmToken() =
        FirebaseMessagingService().unregisterFcmToken()
}