package com.daily.dayo.network.login
import com.daily.dayo.login.LoginRequest
import com.daily.dayo.login.LoginResponse
import com.daily.dayo.login.MemberResponse
import com.daily.dayo.login.model.SignupEmailRequest
import com.daily.dayo.login.model.SignupEmailResponse
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class LoginApiHelperImpl@Inject constructor(private val loginApiService: LoginApiService) : LoginApiHelper{

    override suspend fun requestLogin(request: LoginRequest): LoginResponse =
        loginApiService.requestLogin(request)

    override suspend fun requestMemberInfo() : Response<MemberResponse> =
        loginApiService.requestMemberInfo()

    override suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: MultipartBody.Part
    ): Response<SignupEmailResponse> = loginApiService.requestSignupEmail(email, nickname, password, profileImg)
}