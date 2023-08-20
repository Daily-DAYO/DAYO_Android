package com.daily.dayo.domain.usecase.post

import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestEditPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Int,
        category: Category,
        contents: String,
        folderId: Int,
        hashtags: List<String>
    ) =
        postRepository.requestEditPost(postId, category, contents, folderId, hashtags)
}