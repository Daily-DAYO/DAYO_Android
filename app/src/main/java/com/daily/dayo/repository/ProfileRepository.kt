package com.daily.dayo.repository

import com.daily.dayo.network.profile.ProfileApiHelper
import com.daily.dayo.profile.model.ResponseMyProfile
import com.daily.dayo.profile.model.ResponseOtherProfile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val profileApiHelper: ProfileApiHelper) {
    suspend fun requestMyProfile() : Response<ResponseMyProfile> = profileApiHelper.requestMyProfile()
    suspend fun requestUpdateMyProfile(nickname: String?, profileImg: File?) : Response<Void> {
        var uploadFile : MultipartBody.Part?= null
        if(profileImg != null) {
            val fileNameDivideList: List<String> = profileImg.toString().split("/")
            var requestBodyFile : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), profileImg!!)
            uploadFile = MultipartBody.Part.createFormData("profileImg",fileNameDivideList[fileNameDivideList.size - 1], requestBodyFile)
        } else if(profileImg == null){
            var requestBodyEmpty : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), "")
            uploadFile = MultipartBody.Part.createFormData("profileImg", "", requestBodyEmpty)
        }
        return profileApiHelper.requestUpdateMyProfile(nickname, uploadFile)
    }
    suspend fun requestOtherProfile(memberId:String): Response<ResponseOtherProfile> = profileApiHelper.requestOtherProfile(memberId)
}