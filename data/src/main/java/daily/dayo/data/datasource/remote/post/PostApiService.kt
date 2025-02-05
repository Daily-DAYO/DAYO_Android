package daily.dayo.data.datasource.remote.post

import daily.dayo.domain.model.Category
import daily.dayo.domain.model.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {

    @Multipart
    @POST("/api/v1/posts")
    suspend fun requestUploadPost(
        @Part("category") category: String,
        @Part("contents") contents: String,
        @Part files: List<MultipartBody.Part>,
        @Part("folderId") folderId: Int,
        @Part("tags") tags: Array<String>
    ): NetworkResponse<CreatePostResponse>

    @POST("/api/v1/posts/{postId}/edit")
    suspend fun requestEditPost(
        @Path("postId") postId: Int,
        @Body body: EditPostRequest
    ): NetworkResponse<EditPostResponse>

    @GET("/api/v1/posts")
    suspend fun requestNewPostList(): NetworkResponse<ListAllPostResponse>

    @GET("/api/v1/posts/category/{category}")
    suspend fun requestNewPostListCategory(@Path("category") category: Category): NetworkResponse<ListCategoryPostResponse>

    @GET("/api/v1/posts/dayopick/all")
    suspend fun requestDayoPickPostList(): NetworkResponse<DayoPickPostListResponse>

    @GET("/api/v1/posts/dayopick/{category}")
    suspend fun requestDayoPickPostListCategory(@Path("category") category: Category): NetworkResponse<DayoPickPostListResponse>

    @GET("/api/v1/posts/{postId}")
    suspend fun requestPostDetail(@Path("postId") postId: Int): NetworkResponse<DetailPostResponse>

    @POST("/api/v1/posts/delete/{postId}")
    suspend fun requestDeletePost(@Path("postId") postId: Int): NetworkResponse<Void>

    @GET("/api/v1/posts/feed/list")
    suspend fun requestAllFeedList(@Query("end") end: Int): NetworkResponse<ListFeedResponse>

    @GET("/api/v1/posts/feed/{category}")
    suspend fun requestFeedListByCategory(
        @Path("category") category: Category,
        @Query("end") end: Int
    ): NetworkResponse<ListFeedResponse>
}