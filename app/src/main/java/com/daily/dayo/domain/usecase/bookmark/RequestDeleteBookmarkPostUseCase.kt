package com.daily.dayo.domain.usecase.bookmark

import com.daily.dayo.domain.repository.BookmarkRepository
import javax.inject.Inject

class RequestDeleteBookmarkPostUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(postId: Int) =
        bookmarkRepository.requestDeleteBookmarkPost(postId)
}