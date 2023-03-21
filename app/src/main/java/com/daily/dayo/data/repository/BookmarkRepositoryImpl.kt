package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.bookmark.BookmarkApiService
import com.daily.dayo.data.datasource.remote.bookmark.BookmarkPagingSource
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.BookmarkRepository
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkApiService: BookmarkApiService
) : BookmarkRepository {

    override suspend fun requestBookmarkPost(body: CreateBookmarkRequest): NetworkResponse<CreateBookmarkResponse> =
        bookmarkApiService.requestBookmarkPost(body)

    override suspend fun requestDeleteBookmarkPost(postId: Int): NetworkResponse<Void> =
        bookmarkApiService.requestDeleteBookmarkPost(postId)

    override fun requestAllMyBookmarkPostList() = Pager(PagingConfig(pageSize = BOOKMARK_PAGE_SIZE)) {
        BookmarkPagingSource(bookmarkApiService, BOOKMARK_PAGE_SIZE)
    }.flow

    companion object {
        private const val BOOKMARK_PAGE_SIZE = 10
    }
}