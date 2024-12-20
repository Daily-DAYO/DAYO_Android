package daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import daily.dayo.data.datasource.remote.bookmark.BookmarkApiService
import daily.dayo.data.datasource.remote.bookmark.BookmarkPagingSource
import daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import daily.dayo.data.mapper.toBookmarkPostResponse
import daily.dayo.domain.model.BookmarkPostResponse
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.BookmarkRepository
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkApiService: BookmarkApiService
) : BookmarkRepository {
    override suspend fun requestBookmarkPost(postId: Int): NetworkResponse<BookmarkPostResponse> {
        return when (val response = bookmarkApiService.requestBookmarkPost(CreateBookmarkRequest(postId = postId))) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toBookmarkPostResponse)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }
    }

    override suspend fun requestDeleteBookmarkPost(postId: Int): NetworkResponse<Void> =
        bookmarkApiService.requestDeleteBookmarkPost(postId)

    override suspend fun requestAllMyBookmarkPostList() =
        Pager(PagingConfig(pageSize = BOOKMARK_PAGE_SIZE)) {
            BookmarkPagingSource(bookmarkApiService, BOOKMARK_PAGE_SIZE)
        }.flow

    override suspend fun requestBookmarkCount(): Int {
        return when (val response = bookmarkApiService.requestAllMyBookmarkPostList(INIT_PAGE)) {
            is NetworkResponse.Success -> response.body?.count ?: 0
            else -> 0
        }
    }

    companion object {
        private const val BOOKMARK_PAGE_SIZE = 10
        private const val INIT_PAGE = 0
    }
}