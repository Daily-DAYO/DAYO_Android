package daily.dayo.domain.usecase.comment

import daily.dayo.domain.repository.CommentRepository
import javax.inject.Inject

class RequestDeletePostCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(commentId: Long) =
        commentRepository.requestDeletePostComment(commentId)
}