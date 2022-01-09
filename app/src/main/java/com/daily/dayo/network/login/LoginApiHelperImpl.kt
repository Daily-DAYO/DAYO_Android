package com.daily.dayo.network.login
import com.daily.dayo.login.LoginRequest
import com.daily.dayo.login.LoginResponse
import com.daily.dayo.login.MemberResponse
import retrofit2.Response
import javax.inject.Inject

class LoginApiHelperImpl@Inject constructor(private val loginApiService: LoginApiService) : LoginApiHelper{

    override suspend fun requestLogin(request: LoginRequest): LoginResponse =
        loginApiService.requestLogin(request)

    override suspend fun requestMemberInfo() : Response<MemberResponse> =
        loginApiService.requestMemberInfo()
}