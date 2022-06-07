package com.daily.dayo.domain.usecase.post

import com.daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestDeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Int) =
        postRepository.requestDeletePost(postId)
}