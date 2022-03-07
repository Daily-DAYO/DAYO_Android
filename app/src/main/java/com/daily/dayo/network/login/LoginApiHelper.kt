package com.daily.dayo.network.login

import com.daily.dayo.login.model.*
import okhttp3.MultipartBody
import retrofit2.Response

interface LoginApiHelper {
    suspend fun requestLogin(request: LoginRequest): Response<LoginResponse>
    suspend fun requestMemberInfo() : Response<MemberResponse>
    suspend fun requestCheckEmailDuplicate(email: String) : Response<Void>
    suspend fun requestSignupEmail(email: String, nickname: String, password: String, profileImg: MultipartBody.Part): Response<SignupEmailResponse>
    suspend fun requestRefreshToken():Response<ResponseRefreshToken>
}