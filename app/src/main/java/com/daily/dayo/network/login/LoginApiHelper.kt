package com.daily.dayo.network.login

import com.daily.dayo.login.LoginRequest
import com.daily.dayo.login.LoginResponse
import com.daily.dayo.login.MemberResponse
import com.daily.dayo.login.model.SignupEmailRequest
import com.daily.dayo.login.model.SignupEmailResponse
import okhttp3.MultipartBody
import retrofit2.Response

interface LoginApiHelper {
    suspend fun requestLogin(request: LoginRequest): LoginResponse
    suspend fun requestMemberInfo() : Response<MemberResponse>
    suspend fun requestSignupEmail(email: String, nickname: String, password: String, profileImg: MultipartBody.Part): Response<SignupEmailResponse>
}