package daily.dayo.domain.usecase.bookmark

import daily.dayo.domain.repository.BookmarkRepository
import javax.inject.Inject

class RequestBookmarkPostUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(postId: Long) =
        bookmarkRepository.requestBookmarkPost(postId = postId)
}