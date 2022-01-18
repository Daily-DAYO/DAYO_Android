package com.daily.dayo.network.profile

import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.profile.model.ResponseOtherProfile
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {
    @GET("/api/v1/members/profile/my")
    suspend fun requestMyProfile() : Response<ResponseMyProfile>

    @Multipart
    @POST("/api/v1/members/update/profile")
    suspend fun requestUpdateMyProfile(@Part("nickname") nickname: String?,@Part profileImg: MultipartBody.Part?) : Response<Void>

    @GET("/api/v1/members/profile/other/{memberId}")
    suspend fun requestOtherProfile(@Path("memberId") memberId:String):Response<ResponseOtherProfile>

}