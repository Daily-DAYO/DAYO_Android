package com.daily.dayo.data.datasource.remote.folder

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface FolderApiService {

    @Multipart
    @POST("/api/v1/folders")
    suspend fun requestCreateFolder(
        @Part("name") name: String,
        @Part("privacy") privacy: String,
        @Part("subheading") subheading: String?,
        @Part thumbnailImage: MultipartBody.Part?
    ): Response<CreateFolderResponse>

    @Multipart
    @POST("/api/v1/folders/patch")
    suspend fun requestEditFolder(
        @Part("folderId") folderId: Int,
        @Part("name") name: String,
        @Part("privacy") privacy: String,
        @Part("subheading") subheading: String?,
        @Part("isFileChange") isFileChange: Boolean,
        @Part thumbnailImage: MultipartBody.Part?
    ): Response<EditFolderResponse>

    @GET("/api/v1/folders/list/{memberId}")
    suspend fun requestAllFolderList(@Path("memberId") memberId: String): Response<ListAllFolderResponse>

    @GET("/api/v1/folders/my")
    suspend fun requestAllMyFolderList(): Response<ListAllMyFolderResponse>

    @POST("/api/v1/folders/inPost")
    suspend fun requestCreateFolderInPost(@Body body: CreateFolderInPostRequest): Response<CreateFolderInPostResponse>

    @GET("/api/v1/folders/{folderId}")
    suspend fun requestDetailListFolder(@Path("folderId") folderId: Int): Response<DetailFolderResponse>

    @POST("/api/v1/folders/delete/{folderId}")
    suspend fun requestDeleteFolder(@Path("folderId") folderId: Int): Response<Void>

    @POST("/api/v1/folders/order")
    suspend fun requestOrderFolder(@Body body: List<EditOrderDto>): Response<Void>
}