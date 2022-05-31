package com.daily.dayo.data.datasource.remote.bookmark

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookmarkApiService {

    @POST("/api/v1/bookmark")
    suspend fun requestBookmarkPost(@Body body: CreateBookmarkRequest): Response<CreateBookmarkResponse>

    @POST("/api/v1/bookmark/delete/{postId}")
    suspend fun requestDeleteBookmarkPost(@Path("postId") postId: Int): Response<Void>

    @GET("/api/v1/bookmark/list")
    suspend fun requestAllMyBookmarkPostList(): Response<ListAllMyBookmarkPostResponse>
}