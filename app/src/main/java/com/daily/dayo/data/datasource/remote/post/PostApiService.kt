package com.daily.dayo.data.datasource.remote.post

import com.daily.dayo.domain.model.Category
import okhttp3.MultipartBody
import retrofit2.Response
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
    ): Response<CreatePostResponse>

    @POST("/api/v1/posts/{postId}/edit")
    suspend fun requestEditPost(
        @Path("postId") postId: Int,
        @Body body: EditPostRequest
    ): Response<EditPostResponse>

    @GET("/api/v1/posts")
    suspend fun requestNewPostList(): Response<ListAllPostResponse>

    @GET("/api/v1/posts/category/{category}")
    suspend fun requestNewPostListCategory(@Path("category") category: Category): Response<ListCategoryPostResponse>

    @GET("/api/v1/posts/dayopick/all")
    suspend fun requestDayoPickPostList(): Response<DayoPickPostListResponse>

    @GET("/api/v1/posts/dayopick/{category}")
    suspend fun requestDayoPickPostListCategory(@Path("category") category: Category): Response<DayoPickPostListResponse>

    @GET("/api/v1/posts/{postId}")
    suspend fun requestPostDetail(@Path("postId") postId: Int): Response<DetailPostResponse>

    @POST("/api/v1/posts/delete/{postId}")
    suspend fun requestDeletePost(@Path("postId") postId: Int): Response<Void>

    @GET("/api/v1/posts/feed/list")
    suspend fun requestFeedList(): Response<ListFeedResponse>
}