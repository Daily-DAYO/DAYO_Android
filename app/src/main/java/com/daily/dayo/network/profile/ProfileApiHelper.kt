package com.daily.dayo.network.profile

import com.daily.dayo.profile.model.ResponseBookmarkPostList
import com.daily.dayo.profile.model.ResponseLikePostList
import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.profile.model.ResponseOtherProfile
import okhttp3.MultipartBody
import retrofit2.Response

interface ProfileApiHelper {
    suspend fun requestMyProfile() : Response<ResponseMyProfile>
    suspend fun requestUpdateMyProfile(nickname: String?, profileImg: MultipartBody.Part?) : Response<Void>
    suspend fun requestOtherProfile(memberId:String):Response<ResponseOtherProfile>
    suspend fun requestAllMyLikePostList() : Response<ResponseLikePostList>
    suspend fun requestAllMyBookmarkPostList() : Response<ResponseBookmarkPostList>
}