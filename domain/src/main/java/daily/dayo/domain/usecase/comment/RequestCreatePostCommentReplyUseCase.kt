package daily.dayo.domain.usecase.comment

import daily.dayo.domain.model.MentionUser
import daily.dayo.domain.repository.CommentRepository
import javax.inject.Inject

class RequestCreatePostCommentReplyUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(commentId: Long, contents: String, postId: Int, mentionList: List<MentionUser>) =
        commentRepository.requestCreatePostCommentReply(commentId = commentId, contents = contents, postId = postId, mentionList = mentionList)
}