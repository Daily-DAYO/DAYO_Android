package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.ResponseFolder
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FolderApiService {

    @Multipart
    @POST("/api/v1/folders")
    suspend fun requestCreateFolder(@Part("memberId") numberId:String, @Part("name") name:String, @Part("subheading") subheading:String?, @Part thumbnailImage : MultipartBody.Part?): Response<ResponseFolder>
}