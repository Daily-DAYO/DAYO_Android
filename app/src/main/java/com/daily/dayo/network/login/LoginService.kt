package com.daily.dayo.network.login

import com.daily.dayo.login.LoginRequest
import com.daily.dayo.login.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("/api/v1/members/kakaoOAuth")
    suspend fun requestLogin(@Body body : LoginRequest) : LoginResponse
}