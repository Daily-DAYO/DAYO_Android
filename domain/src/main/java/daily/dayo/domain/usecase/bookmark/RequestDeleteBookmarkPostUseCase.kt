package daily.dayo.domain.usecase.bookmark

import daily.dayo.domain.repository.BookmarkRepository
import javax.inject.Inject

class RequestDeleteBookmarkPostUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(postId: Long) =
        bookmarkRepository.requestDeleteBookmarkPost(postId)
}