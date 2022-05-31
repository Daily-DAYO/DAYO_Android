package com.daily.dayo.domain.usecase.post

import com.daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestDayoPickPostListUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke() =
        postRepository.requestDayoPickPostList()
}