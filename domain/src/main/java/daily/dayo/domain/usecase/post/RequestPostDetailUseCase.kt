package daily.dayo.domain.usecase.post

import daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestPostDetailUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Int) =
        postRepository.requestPostDetail(postId)
}