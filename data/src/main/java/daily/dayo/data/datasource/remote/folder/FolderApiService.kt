package daily.dayo.data.datasource.remote.folder

import daily.dayo.domain.model.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    // 폴더 리스트
    @GET("/api/v2/folders/list/{memberId}")
    suspend fun requestAllFolderList(@Path("memberId") memberId: String): NetworkResponse<ListAllFolderResponse>

    @GET("/api/v2/folders/my")
    suspend fun requestAllMyFolderList(): NetworkResponse<ListAllMyFolderResponse>

    // 폴더 정보
    @GET("/api/v2/folders/{folderId}/info")
    suspend fun requestFolderInfo(@Path("folderId") folderId: Int): NetworkResponse<FolderInfoResponse>

    // 폴더 내 게시글
    @GET("/api/v2/folders/{folderId}")
    suspend fun requestDetailListFolder(
        @Path("folderId") folderId: Int,
        @Query("end") end: Int,
        @Query("order") order: String
    ): NetworkResponse<DetailFolderResponse>
}