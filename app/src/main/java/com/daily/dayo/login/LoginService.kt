package com.daily.dayo.login

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("/api/v1/members/kakaoOAuth")
    suspend fun requestLogin(@Body body : LoginRequest) : LoginResponse
}