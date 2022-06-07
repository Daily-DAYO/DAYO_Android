package com.daily.dayo.domain.usecase.post

import com.daily.dayo.data.datasource.remote.post.EditPostRequest
import com.daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestEditPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Int,
        request: EditPostRequest
    ) =
        postRepository.requestEditPost(postId, request)
}