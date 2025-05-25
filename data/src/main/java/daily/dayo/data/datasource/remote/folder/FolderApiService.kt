package daily.dayo.data.datasource.remote.folder

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
        @Part("folderId") folderId: Long,
        @Part("name") name: String,
        @Part("privacy") privacy: String,
        @Part("subheading") subheading: String?,
        @Part("isFileChange") isFileChange: Boolean,
        @Part thumbnailImage: MultipartBody.Part?
    ): NetworkResponse<EditFolderResponse>

    @POST("/api/v1/folders/inPost")
    suspend fun requestCreateFolderInPost(@Body body: CreateFolderInPostRequest): NetworkResponse<CreateFolderInPostResponse>

    @POST("/api/v1/folders/delete/{folderId}")
    suspend fun requestDeleteFolder(@Path("folderId") folderId: Long): NetworkResponse<Void>

    @POST("/api/v2/folders/move")
    suspend fun requestFolderMove(@Body body: FolderMoveRequest): NetworkResponse<Void>

    // 폴더 리스트
    @GET("/api/v2/folders/list/{memberId}")
    suspend fun requestAllFolderList(@Path("memberId") memberId: String): NetworkResponse<ListAllFolderResponse>

    @GET("/api/v2/folders/my")
    suspend fun requestAllMyFolderList(): NetworkResponse<ListAllMyFolderResponse>

    // 폴더 정보
    @GET("/api/v2/folders/{folderId}/info")
    suspend fun requestFolderInfo(@Path("folderId") folderId: Long): NetworkResponse<FolderInfoResponse>

    // 폴더 내 게시글
    @GET("/api/v2/folders/{folderId}")
    suspend fun requestDetailListFolder(
        @Path("folderId") folderId: Long,
        @Query("end") end: Int,
        @Query("order") order: String
    ): NetworkResponse<DetailFolderResponse>
}