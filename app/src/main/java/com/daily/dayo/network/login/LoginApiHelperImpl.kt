package com.daily.dayo.network.login

import com.daily.dayo.login.model.*
import com.daily.dayo.setting.model.RequestChangePassword
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class LoginApiHelperImpl @Inject constructor(private val loginApiService: LoginApiService) :
    LoginApiHelper {

    override suspend fun requestLoginKakao(request: LoginRequestKakao): Response<LoginResponse> =
        loginApiService.requestLoginKakao(request)

    override suspend fun requestLoginEmail(request: LoginRequestEmail): Response<LoginResponse> =
        loginApiService.requestLoginEmail(request)

    override suspend fun requestMemberInfo(): Response<MemberResponse> =
        loginApiService.requestMemberInfo()

    override suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part
    ): Response<SignupEmailResponse> =
        loginApiService.requestSignupEmail(email, nickname, password, profileImg)

    override suspend fun requestCheckEmailDuplicate(email: String): Response<Void> =
        loginApiService.requestCheckEmailDuplicate(email)

    override suspend fun requestCertificateEmail(email: String): Response<SignupEmailAuthCodeResponse> =
        loginApiService.requestCertificateEmail(email)

    override suspend fun requestRefreshToken(): Response<ResponseRefreshToken> =
        loginApiService.requestRefreshToken()

    override suspend fun requestDeviceToken(request: RequestDeviceToken): Response<Void> =
        loginApiService.requestDeviceToken(request)

    override suspend fun requestChangeLostPassword(request: RequestChangePassword): Response<Void> =
        loginApiService.requestChangeLostPassword(request)
}