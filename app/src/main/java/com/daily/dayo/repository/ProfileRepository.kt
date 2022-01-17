package com.daily.dayo.repository

import com.daily.dayo.network.profile.ProfileApiHelper
import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.profile.model.ResponseOtherProfile
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val profileApiHelper: ProfileApiHelper) {
    suspend fun requestMyProfile() : Response<ResponseMyProfile> = profileApiHelper.requestMyProfile()
    suspend fun requestOtherProfile(memberId:String): Response<ResponseOtherProfile> = profileApiHelper.requestOtherProfile(memberId)
}