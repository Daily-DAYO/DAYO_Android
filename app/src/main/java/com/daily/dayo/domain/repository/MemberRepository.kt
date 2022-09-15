package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.member.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Query

interface MemberRepository {

    suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part
    ): Response<MemberSignupResponse>

    suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?
    ): Response<Void>

    suspend fun requestLoginKakao(body: MemberOAuthRequest): Response<MemberOAuthResponse>
    suspend fun requestLoginEmail(body: MemberSignInRequest): Response<MemberSignInResponse>
    suspend fun requestMemberInfo(): Response<MemberInfoResponse>
    suspend fun requestCheckEmailDuplicate(email: String): Response<Void>
    suspend fun requestCertificateEmail(email: String): Response<MemberAuthCodeResponse>
    suspend fun requestRefreshToken(): Response<RefreshTokenResponse>
    suspend fun requestDeviceToken(body: DeviceTokenRequest): Response<Void>
    suspend fun requestMyProfile(): Response<MemberMyProfileResponse>
    suspend fun requestOtherProfile(memberId: String): Response<MemberOtherProfileResponse>
    suspend fun requestResign(content: String): Response<Void>
    suspend fun requestReceiveAlarm(): Response<ReceiveAlarmResponse>
    suspend fun requestChangeReceiveAlarm(body: ChangeReceiveAlarmRequest): Response<Void>
    suspend fun requestLogout(): Response<Void>
    suspend fun requestCheckEmail(email: String): Response<Void>
    suspend fun requestCheckEmailAuth(email: String): Response<MemberAuthCodeResponse>
    suspend fun requestCheckCurrentPassword(body: CheckPasswordRequest): Response<Void>
    suspend fun requestChangePassword(body: ChangePasswordRequest): Response<Void>
    suspend fun requestSettingChangePassword(body: ChangePasswordRequest): Response<Void>

    // Firebase Messaging Service
    suspend fun getCurrentFcmToken(): String
    suspend fun registerFcmToken()
    fun unregisterFcmToken()
}