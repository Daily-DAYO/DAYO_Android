package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.member.*
import com.daily.dayo.domain.model.NetworkResponse
import okhttp3.MultipartBody

interface MemberRepository {

    suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part
    ): NetworkResponse<MemberSignupResponse>

    suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?,
        onBasicProfileImg: Boolean
    ): NetworkResponse<Void>

    suspend fun requestLoginKakao(body: MemberOAuthRequest): NetworkResponse<MemberOAuthResponse>
    suspend fun requestLoginEmail(body: MemberSignInRequest): NetworkResponse<MemberSignInResponse>
    suspend fun requestMemberInfo(): NetworkResponse<MemberInfoResponse>
    suspend fun requestCheckEmailDuplicate(email: String): NetworkResponse<Void>
    suspend fun requestCertificateEmail(email: String): NetworkResponse<MemberAuthCodeResponse>
    suspend fun requestRefreshToken(): NetworkResponse<RefreshTokenResponse>
    suspend fun requestDeviceToken(body: DeviceTokenRequest): NetworkResponse<Void>
    suspend fun requestMyProfile(): NetworkResponse<MemberMyProfileResponse>
    suspend fun requestOtherProfile(memberId: String): NetworkResponse<MemberOtherProfileResponse>
    suspend fun requestResign(content: String): NetworkResponse<Void>
    suspend fun requestReceiveAlarm(): NetworkResponse<ReceiveAlarmResponse>
    suspend fun requestChangeReceiveAlarm(body: ChangeReceiveAlarmRequest): NetworkResponse<Void>
    suspend fun requestLogout(): NetworkResponse<Void>
    suspend fun requestCheckEmail(email: String): NetworkResponse<Void>
    suspend fun requestCheckEmailAuth(email: String): NetworkResponse<MemberAuthCodeResponse>
    suspend fun requestCheckCurrentPassword(body: CheckPasswordRequest): NetworkResponse<Void>
    suspend fun requestChangePassword(body: ChangePasswordRequest): NetworkResponse<Void>
    suspend fun requestSettingChangePassword(body: ChangePasswordRequest): NetworkResponse<Void>

    // Firebase Messaging Service
    suspend fun getCurrentFcmToken(): String
    suspend fun registerFcmToken()
    fun unregisterFcmToken()
}