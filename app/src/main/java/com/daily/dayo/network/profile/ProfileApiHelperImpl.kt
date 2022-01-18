package com.daily.dayo.network.profile

import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.profile.model.ResponseOtherProfile
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ProfileApiHelperImpl @Inject constructor(private val profileApiService: ProfileApiService) :ProfileApiHelper{
   override suspend fun requestMyProfile() : Response<ResponseMyProfile> = profileApiService.requestMyProfile()
   override suspend fun requestUpdateMyProfile(nickname: String?, profileImg: MultipartBody.Part?): Response<Void> = profileApiService.requestUpdateMyProfile(nickname, profileImg)
   override suspend fun requestOtherProfile(memberId:String):Response<ResponseOtherProfile> = profileApiService.requestOtherProfile(memberId)

}