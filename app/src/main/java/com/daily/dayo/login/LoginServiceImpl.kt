package com.daily.dayo.login
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginServiceImpl {

    private val BASE_URL = "http://www.endlesscreation.kr:8080"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val loginService: LoginService = retrofit.create(LoginService::class.java)

    suspend fun requestLogin(request: LoginRequest): LoginResponse =
        loginService.requestLogin(request)
}