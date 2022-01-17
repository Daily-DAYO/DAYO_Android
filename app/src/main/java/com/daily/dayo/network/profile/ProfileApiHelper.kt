package com.daily.dayo.network.profile

import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.profile.model.ResponseOtherProfile
import retrofit2.Response

interface ProfileApiHelper {
    suspend fun requestMyProfile() : Response<ResponseMyProfile>
    suspend fun requestOtherProfile(memberId:String):Response<ResponseOtherProfile>
}