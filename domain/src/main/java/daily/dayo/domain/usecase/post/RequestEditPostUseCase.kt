package daily.dayo.domain.usecase.post

import daily.dayo.domain.model.Category
import daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestEditPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Long,
        category: Category,
        contents: String,
        folderId: Long,
        hashtags: List<String>
    ) =
        postRepository.requestEditPost(postId, category, contents, folderId, hashtags)
}