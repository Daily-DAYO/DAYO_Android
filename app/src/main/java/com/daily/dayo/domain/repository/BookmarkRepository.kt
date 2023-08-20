package com.daily.dayo.domain.repository

import androidx.paging.PagingData
import com.daily.dayo.domain.model.BookmarkPost
import com.daily.dayo.domain.model.BookmarkPostResponse
import com.daily.dayo.domain.model.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    
    suspend fun requestBookmarkPost(postId: Int): NetworkResponse<BookmarkPostResponse>
    suspend fun requestDeleteBookmarkPost(postId: Int): NetworkResponse<Void>
    suspend fun requestAllMyBookmarkPostList(): Flow<PagingData<BookmarkPost>>
}