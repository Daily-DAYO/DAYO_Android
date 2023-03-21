package com.daily.dayo.domain.usecase.post

import com.daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestFeedListUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke() =
        postRepository.requestFeedList()
}