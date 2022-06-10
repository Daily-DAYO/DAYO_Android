package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.member.*
import com.daily.dayo.domain.repository.MemberRepository
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Query
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberApiService: MemberApiService
) : MemberRepository {

    override suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part
    ): Response<MemberSignupResponse> =
        memberApiService.requestSignupEmail(email, nickname, password, profileImg)

    override suspend fun requestUpdateMyProfile(
        nickname: String?,
        profileImg: MultipartBody.Part?
    ): Response<Void> =
        memberApiService.requestUpdateMyProfile(nickname, profileImg)

    override suspend fun requestLoginKakao(body: MemberOAuthRequest): Response<MemberOAuthResponse> =
        memberApiService.requestLoginKakao(body)

    override suspend fun requestLoginEmail(body: MemberSignInRequest): Response<MemberSignInResponse> =
        memberApiService.requestLoginEmail(body)

    override suspend fun requestMemberInfo(): Response<MemberInfoResponse> =
        memberApiService.requestMemberInfo()

    override suspend fun requestCheckEmailDuplicate(email: String): Response<Void> =
        memberApiService.requestCheckEmailDuplicate(email)

    override suspend fun requestCertificateEmail(email: String): Response<MemberAuthCodeResponse> =
        memberApiService.requestCertificateEmail(email)

    override suspend fun requestRefreshToken(): Response<RefreshTokenResponse> =
        memberApiService.requestRefreshToken()

    override suspend fun requestDeviceToken(body: DeviceTokenRequest): Response<Void> =
        memberApiService.requestDeviceToken(body)

    override suspend fun requestMyProfile(): Response<MemberMyProfileResponse> =
        memberApiService.requestMyProfile()

    override suspend fun requestOtherProfile(memberId: String): Response<MemberOtherProfileResponse> =
        memberApiService.requestOtherProfile(memberId)

    override suspend fun requestResign(content: String): Response<Void> =
        memberApiService.requestResign(content)
}