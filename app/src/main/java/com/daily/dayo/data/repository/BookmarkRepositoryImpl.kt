package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.bookmark.BookmarkApiService
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.data.datasource.remote.bookmark.ListAllMyBookmarkPostResponse
import com.daily.dayo.domain.repository.BookmarkRepository
import retrofit2.Response
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkApiService: BookmarkApiService
) : BookmarkRepository {

    override suspend fun requestBookmarkPost(body: CreateBookmarkRequest): Response<CreateBookmarkResponse> =
        bookmarkApiService.requestBookmarkPost(body)

    override suspend fun requestDeleteBookmarkPost(postId: Int): Response<Void> =
        bookmarkApiService.requestDeleteBookmarkPost(postId)

    override suspend fun requestAllMyBookmarkPostList(): Response<ListAllMyBookmarkPostResponse> =
        bookmarkApiService.requestAllMyBookmarkPostList()
}