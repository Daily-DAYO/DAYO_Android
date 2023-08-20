package com.daily.dayo.domain.usecase.comment

import com.daily.dayo.domain.repository.CommentRepository
import javax.inject.Inject

class RequestCreatePostCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(contents: String, postId: Int) =
        commentRepository.requestCreatePostComment(contents = contents, postId = postId)
}