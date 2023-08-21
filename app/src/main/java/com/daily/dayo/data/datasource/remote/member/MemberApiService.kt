package com.daily.dayo.data.datasource.remote.member

import daily.dayo.domain.model.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface MemberApiService {

    @Multipart
    @POST("/api/v1/members/signUp")
    suspend fun requestSignupEmail(
        @Part("email") email: String,
        @Part("nickname") nickname: String,
        @Part("password") password: String,
        @Part profileImg: MultipartBody.Part?
    ): NetworkResponse<MemberSignupResponse>

    @Multipart
    @POST("/api/v1/members/update/profile")
    suspend fun requestUpdateMyProfile(
        @Part("nickname") nickname: String?,
        @Part profileImg: MultipartBody.Part?,
        @Part("onBasicProfileImg") onBasicProfileImg: Boolean
    ): NetworkResponse<Void>

    @POST("/api/v1/members/kakaoOAuth")
    suspend fun requestLoginKakao(@Body body: MemberOAuthRequest): NetworkResponse<MemberOAuthResponse>

    @POST("/api/v1/members/signIn")
    suspend fun requestLoginEmail(@Body body: MemberSignInRequest): NetworkResponse<MemberSignInResponse>

    @GET("/api/v1/members/myInfo")
    suspend fun requestMemberInfo(): NetworkResponse<MemberInfoResponse>

    @GET("/api/v1/members/duplicate/email/{email}")
    suspend fun requestCheckEmailDuplicate(@Path("email") email: String): NetworkResponse<Void>

    @GET("/api/v1/members/check")
    suspend fun requestCheckNicknameDuplicate(@Query("nickname") content: String): NetworkResponse<Void>

    @GET("/api/v1/members/signUp/{email}")
    suspend fun requestCertificateEmail(@Path("email") email: String): NetworkResponse<MemberAuthCodeResponse>

    @GET("/api/v1/members/refresh")
    suspend fun requestRefreshToken(): NetworkResponse<RefreshTokenResponse>

    @POST("/api/v1/members")
    suspend fun requestDeviceToken(@Body body: DeviceTokenRequest): NetworkResponse<Void>

    @GET("/api/v1/members/profile/my")
    suspend fun requestMyProfile(): NetworkResponse<MemberMyProfileResponse>

    @GET("/api/v1/members/profile/other/{memberId}")
    suspend fun requestOtherProfile(@Path("memberId") memberId: String): NetworkResponse<MemberOtherProfileResponse>

    @POST("/api/v1/members/resign")
    suspend fun requestResign(@Query("content") content: String): NetworkResponse<Void>

    @GET("/api/v1/members/receiveAlarm")
    suspend fun requestReceiveAlarm(): NetworkResponse<ReceiveAlarmResponse>

    @POST("/api/v1/members/changeReceiveAlarm")
    suspend fun requestChangeReceiveAlarm(@Body body: ChangeReceiveAlarmRequest): NetworkResponse<Void>

    @POST("/api/v1/members/logout")
    suspend fun requestLogout(): NetworkResponse<Void>

    @GET("/api/v1/members/search/{email}")
    suspend fun requestCheckEmail(@Path("email") email: String): NetworkResponse<Void>

    @GET("/api/v1/members/search/code/{email}")
    suspend fun requestCheckEmailAuth(@Path("email") email: String): NetworkResponse<MemberAuthCodeResponse>

    @POST("/api/v1/members/checkPassword")
    suspend fun requestCheckCurrentPassword(@Body body: CheckPasswordRequest): NetworkResponse<Void>

    @POST("/api/v1/members/changePassword")
    suspend fun requestChangePassword(@Body body: ChangePasswordRequest): NetworkResponse<Void>

    @POST("/api/v1/members/setting/changePassword")
    suspend fun requestSettingChangePassword(@Body body: ChangePasswordRequest): NetworkResponse<Void>

    @GET("/api/v1/members/block")
    suspend fun requestBlockList(): NetworkResponse<MemberBlockResponse>
}