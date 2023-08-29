package daily.dayo.domain.usecase.bookmark

import daily.dayo.domain.repository.BookmarkRepository
import javax.inject.Inject

class RequestAllMyBookmarkPostListUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke() =
        bookmarkRepository.requestAllMyBookmarkPostList()
}