package com.daily.dayo.network.login

import com.daily.dayo.login.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface LoginApiService {
    @POST("/api/v1/members/kakaoOAuth")
    suspend fun requestLogin(@Body body : LoginRequest) : Response<LoginResponse>

    @GET("/api/v1/members/myInfo")
    suspend fun requestMemberInfo() : Response<MemberResponse>

    @Multipart
    @POST("/api/v1/members/signUp")
    suspend fun requestSignupEmail(@Part("email") email: String,
                                   @Part("nickname") nickname: String,
                                   @Part("password") password: String,
                                   @Part profileImg: MultipartBody.Part) : Response<SignupEmailResponse>

    @GET("/api/v1/members/refresh")
    suspend fun requestRefreshToken():Response<ResponseRefreshToken>
}