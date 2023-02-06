package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.data.datasource.remote.bookmark.ListAllMyBookmarkPostResponse
import com.daily.dayo.domain.model.NetworkResponse

interface BookmarkRepository {
    
    suspend fun requestBookmarkPost(body: CreateBookmarkRequest): NetworkResponse<CreateBookmarkResponse>
    suspend fun requestDeleteBookmarkPost(postId: Int): NetworkResponse<Void>
    suspend fun requestAllMyBookmarkPostList(): NetworkResponse<ListAllMyBookmarkPostResponse>
}