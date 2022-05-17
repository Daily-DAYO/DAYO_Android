package com.daily.dayo.network.login

import com.daily.dayo.login.model.*
import com.daily.dayo.setting.model.RequestChangePassword
import okhttp3.MultipartBody
import retrofit2.Response

interface LoginApiHelper {
    suspend fun requestLoginKakao(request: LoginRequestKakao): Response<LoginResponse>
    suspend fun requestLoginEmail(request: LoginRequestEmail): Response<LoginResponse>
    suspend fun requestMemberInfo() : Response<MemberResponse>
    suspend fun requestCheckEmailDuplicate(email: String) : Response<Void>
    suspend fun requestSignupEmail(email: String, nickname: String, password: String, profileImg: MultipartBody.Part): Response<SignupEmailResponse>
    suspend fun requestCertificateEmail(email: String) : Response<SignupEmailAuthCodeResponse>
    suspend fun requestRefreshToken():Response<ResponseRefreshToken>
    suspend fun requestDeviceToken(request: RequestDeviceToken) : Response<Void>
    suspend fun requestChangeLostPassword(request: RequestChangePassword) : Response<Void>
}