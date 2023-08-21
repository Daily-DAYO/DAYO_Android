package daily.dayo.domain.usecase.comment

import daily.dayo.domain.repository.CommentRepository
import javax.inject.Inject

class RequestPostCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(postId: Int) =
        commentRepository.requestPostComment(postId)
}