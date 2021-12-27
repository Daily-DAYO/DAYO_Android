package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.ResponseFolder
import okhttp3.MultipartBody
import retrofit2.Response

interface FolderApiHelper {
    suspend fun requestCreateFolder(memberId : String, name:String, subheading:String?, thumbnailImage: MultipartBody.Part?): Response<ResponseFolder>
}