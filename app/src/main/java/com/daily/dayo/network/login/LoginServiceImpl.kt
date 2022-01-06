package com.daily.dayo.network.login
import com.daily.dayo.login.LoginRequest
import com.daily.dayo.login.LoginResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LoginServiceImpl @Inject constructor() : LoginService{

    private val BASE_URL = "http://www.endlesscreation.kr:8080"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val loginService: LoginService = retrofit.create(LoginService::class.java)

    override suspend fun requestLogin(request: LoginRequest): LoginResponse =
        loginService.requestLogin(request)
}