package com.daily.dayo.domain.usecase.bookmark

import com.daily.dayo.data.datasource.remote.bookmark.CreateBookmarkRequest
import com.daily.dayo.domain.repository.BookmarkRepository
import javax.inject.Inject

class RequestBookmarkPostUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(body: CreateBookmarkRequest) =
        bookmarkRepository.requestBookmarkPost(body)
}