package com.daily.dayo.network.login

import com.daily.dayo.login.LoginRequest
import com.daily.dayo.login.LoginResponse
import com.daily.dayo.login.MemberResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApiService {
    @POST("/api/v1/members/kakaoOAuth")
    suspend fun requestLogin(@Body body : LoginRequest) : LoginResponse

    @GET("/api/v1/members/myInfo")
    suspend fun requestMemberInfo() : Response<MemberResponse>
}