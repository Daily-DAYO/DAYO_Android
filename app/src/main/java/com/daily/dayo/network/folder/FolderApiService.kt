package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.ResponseAllFolderList
import com.daily.dayo.profile.model.ResponseCreateFolder
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface FolderApiService {

    @Multipart
    @POST("/api/v1/folders")
    suspend fun requestCreateFolder(@Part("memberId") numberId:String, @Part("name") name:String, @Part("subheading") subheading:String?, @Part thumbnailImage : MultipartBody.Part?): Response<ResponseCreateFolder>

    @GET("/api/v1/folders/{memberId}")
    suspend fun requestAllFolderList(@Path("memberId") memberId:String) :Response<ResponseAllFolderList>

}