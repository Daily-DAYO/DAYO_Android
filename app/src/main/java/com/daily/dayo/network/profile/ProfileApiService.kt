package com.daily.dayo.network.profile

import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.profile.model.ResponseOtherProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    @GET("/api/v1/members/profile/my")
    suspend fun requestMyProfile() : Response<ResponseMyProfile>

    @GET("/api/v1/members/profile/other/{memberId}")
    suspend fun requestOtherProfile(@Path("memberId") memberId:String):Response<ResponseOtherProfile>

}