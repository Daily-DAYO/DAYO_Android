package com.daily.dayo.network.folder

import com.daily.dayo.profile.model.RequestCreateFolderInPost
import com.daily.dayo.profile.model.ResponseAllFolderList
import com.daily.dayo.profile.model.ResponseAllMyFolderList
import com.daily.dayo.profile.model.ResponseFolderId
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface FolderApiService {

    @Multipart
    @POST("/api/v1/folders")
    suspend fun requestCreateFolder(@Part("name") name:String,
                                    @Part("privacy") privacy:String,
                                    @Part("subheading") subheading:String?,
                                    @Part thumbnailImage : MultipartBody.Part?): Response<ResponseFolderId>

    @GET("/api/v1/folders/list/{memberId}")
    suspend fun requestAllFolderList(@Path("memberId") memberId:String) :Response<ResponseAllFolderList>

    @GET("/api/v1/folders/my")
    suspend fun requestAllMyFolderList() : Response<ResponseAllMyFolderList>

    @POST("/api/v1/folders/inPost")
    suspend fun requestCreateFolderInPost(@Body body:RequestCreateFolderInPost) : Response<ResponseFolderId>

}