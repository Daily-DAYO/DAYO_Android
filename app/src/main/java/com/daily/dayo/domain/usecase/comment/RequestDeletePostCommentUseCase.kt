package com.daily.dayo.domain.usecase.comment

import com.daily.dayo.domain.repository.CommentRepository
import javax.inject.Inject

class RequestDeletePostCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(commentId: Int) =
        commentRepository.requestDeletePostComment(commentId)
}