package daily.dayo.domain.usecase.post

import daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestDeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Long) =
        postRepository.requestDeletePost(postId)
}