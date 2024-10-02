package daily.dayo.domain.usecase.comment

import daily.dayo.domain.model.MentionUser
import daily.dayo.domain.repository.CommentRepository
import javax.inject.Inject

class RequestCreatePostCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(contents: String, postId: Int, mentionList: List<MentionUser>) =
        commentRepository.requestCreatePostComment(contents = contents, postId = postId, mentionList = mentionList)
}