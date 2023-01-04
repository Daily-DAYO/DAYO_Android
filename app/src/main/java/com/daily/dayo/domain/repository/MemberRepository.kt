package com.daily.dayo.domain.repository

import com.daily.dayo.common.Resource
import com.daily.dayo.data.datasource.remote.member.*
import okhttp3.MultipartBody
import retrofit2.Response

interface MemberRepository {

    suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part
    ): Resource<MemberSignupResponse>

    suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?,
        onBasicProfileImg: Boolean
    ): Resource<Void>

    suspend fun requestLoginKakao(body: MemberOAuthRequest): Resource<MemberOAuthResponse>
    suspend fun requestLoginEmail(body: MemberSignInRequest): Resource<MemberSignInResponse>
    suspend fun requestMemberInfo(): Resource<MemberInfoResponse>
    suspend fun requestCheckEmailDuplicate(email: String): Resource<Void>
    suspend fun requestCertificateEmail(email: String): Resource<MemberAuthCodeResponse>
    suspend fun requestRefreshToken(): Resource<RefreshTokenResponse>
    suspend fun requestDeviceToken(body: DeviceTokenRequest): Resource<Void>
    suspend fun requestMyProfile(): Resource<MemberMyProfileResponse>
    suspend fun requestOtherProfile(memberId: String): Resource<MemberOtherProfileResponse>
    suspend fun requestResign(content: String): Resource<Void>
    suspend fun requestReceiveAlarm(): Resource<ReceiveAlarmResponse>
    suspend fun requestChangeReceiveAlarm(body: ChangeReceiveAlarmRequest): Resource<Void>
    suspend fun requestLogout(): Resource<Void>
    suspend fun requestCheckEmail(email: String): Resource<Void>
    suspend fun requestCheckEmailAuth(email: String): Resource<MemberAuthCodeResponse>
    suspend fun requestCheckCurrentPassword(body: CheckPasswordRequest): Resource<Void>
    suspend fun requestChangePassword(body: ChangePasswordRequest): Resource<Void>
    suspend fun requestSettingChangePassword(body: ChangePasswordRequest): Resource<Void>

    // Firebase Messaging Service
    suspend fun getCurrentFcmToken(): String
    suspend fun registerFcmToken()
    fun unregisterFcmToken()
}