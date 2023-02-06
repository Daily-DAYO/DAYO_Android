package com.daily.dayo.data.datasource.remote.bookmark

import com.daily.dayo.domain.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookmarkApiService {

    @POST("/api/v1/bookmark")
    suspend fun requestBookmarkPost(@Body body: CreateBookmarkRequest): NetworkResponse<CreateBookmarkResponse>

    @POST("/api/v1/bookmark/delete/{postId}")
    suspend fun requestDeleteBookmarkPost(@Path("postId") postId: Int): NetworkResponse<Void>

    @GET("/api/v1/bookmark/list")
    suspend fun requestAllMyBookmarkPostList(): NetworkResponse<ListAllMyBookmarkPostResponse>
}