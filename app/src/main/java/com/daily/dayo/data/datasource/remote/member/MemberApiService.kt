package com.daily.dayo.data.datasource.remote.member

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MemberApiService {

    @Multipart
    @POST("/api/v1/members/signUp")
    suspend fun requestSignupEmail(
        @Part("email") email: String,
        @Part("nickname") nickname: String,
        @Part("password") password: String,
        @Part profileImg: MultipartBody.Part
    ): Response<MemberSignupResponse>

    @Multipart
    @POST("/api/v1/members/update/profile")
    suspend fun requestUpdateMyProfile(
        @Part("nickname") nickname: String?,
        @Part profileImg: MultipartBody.Part?
    ): Response<Void>

    @POST("/api/v1/members/kakaoOAuth")
    suspend fun requestLoginKakao(@Body body: MemberOAuthRequest): Response<MemberOAuthResponse>

    @POST("/api/v1/members/signIn")
    suspend fun requestLoginEmail(@Body body: MemberSignInRequest): Response<MemberSignInResponse>

    @GET("/api/v1/members/myInfo")
    suspend fun requestMemberInfo(): Response<MemberInfoResponse>

    @GET("/api/v1/members/duplicate/email/{email}")
    suspend fun requestCheckEmailDuplicate(@Path("email") email: String): Response<Void>

    @GET("/api/v1/members/signUp/{email}")
    suspend fun requestCertificateEmail(@Path("email") email: String): Response<MemberAuthCodeResponse>

    @GET("/api/v1/members/refresh")
    suspend fun requestRefreshToken(): Response<RefreshTokenResponse>

    @POST("/api/v1/members")
    suspend fun requestDeviceToken(@Body body: DeviceTokenRequest): Response<Void>

    @GET("/api/v1/members/profile/my")
    suspend fun requestMyProfile(): Response<MemberMyProfileResponse>

    @GET("/api/v1/members/profile/other/{memberId}")
    suspend fun requestOtherProfile(@Path("memberId") memberId: String): Response<MemberOtherProfileResponse>
}