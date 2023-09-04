package daily.dayo.domain.usecase.post

import daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestNewPostListUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke() =
        postRepository.requestNewPostList()
}