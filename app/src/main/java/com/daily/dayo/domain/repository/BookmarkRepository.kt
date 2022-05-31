package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.data.datasource.remote.bookmark.ListAllMyBookmarkPostResponse
import retrofit2.Response

interface BookmarkRepository {
    
    suspend fun requestBookmarkPost(body: CreateBookmarkRequest): Response<CreateBookmarkResponse>
    suspend fun requestDeleteBookmarkPost(postId: Int): Response<Void>
    suspend fun requestAllMyBookmarkPostList(): Response<ListAllMyBookmarkPostResponse>
}