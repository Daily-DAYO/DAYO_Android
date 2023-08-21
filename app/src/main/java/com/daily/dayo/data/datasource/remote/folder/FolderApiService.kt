package com.daily.dayo.data.datasource.remote.folder

import daily.dayo.domain.model.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface FolderApiService {

    @Multipart
    @POST("/api/v1/folders")
    suspend fun requestCreateFolder(
        @Part("name") name: String,
        @Part("privacy") privacy: String,
        @Part("subheading") subheading: String?,
        @Part thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<CreateFolderResponse>

    @Multipart
    @POST("/api/v1/folders/patch")
    suspend fun requestEditFolder(
        @Part("folderId") folderId: Int,
        @Part("name") name: String,
        @Part("privacy") privacy: String,
        @Part("subheading") subheading: String?,
        @Part("isFileChange") isFileChange: Boolean,
        @Part thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<EditFolderResponse>

    @POST("/api/v1/folders/inPost")
    suspend fun requestCreateFolderInPost(@Body body: CreateFolderInPostRequest): NetworkResponse<CreateFolderInPostResponse>

    @POST("/api/v1/folders/delete/{folderId}")
    suspend fun requestDeleteFolder(@Path("folderId") folderId: Int): NetworkResponse<Void>

    @POST("/api/v1/folders/order")
    suspend fun requestOrderFolder(@Body body: List<EditOrderDto>): NetworkResponse<Void>

    // 폴더 리스트
    @GET("/api/v1/folders/list/{memberId}")
    suspend fun requestAllFolderList(@Path("memberId") memberId: String): NetworkResponse<ListAllFolderResponse>

    @GET("/api/v1/folders/my")
    suspend fun requestAllMyFolderList(): NetworkResponse<ListAllMyFolderResponse>

    // 폴더 정보
    @GET("/api/v1/folders/{folderId}/info")
    suspend fun requestFolderInfo(@Path("folderId") folderId: Int): NetworkResponse<FolderInfoResponse>

    // 폴더 내 게시글
    @GET("/api/v1/folders/{folderId}")
    suspend fun requestDetailListFolder(@Path("folderId") folderId: Int, @Query("end") end: Int): NetworkResponse<DetailFolderResponse>
}