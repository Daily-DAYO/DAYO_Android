package com.daily.dayo.data.datasource.remote.post

import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.*

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
    suspend fun requestFeedList(@Query("end") end: Int): NetworkResponse<ListFeedResponse>
}