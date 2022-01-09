package com.daily.dayo.network.login

import com.daily.dayo.login.LoginRequest
import com.daily.dayo.login.LoginResponse
import com.daily.dayo.login.MemberResponse
import retrofit2.Response

interface LoginApiHelper {
    suspend fun requestLogin(request: LoginRequest): LoginResponse
    suspend fun requestMemberInfo() : Response<MemberResponse>
}