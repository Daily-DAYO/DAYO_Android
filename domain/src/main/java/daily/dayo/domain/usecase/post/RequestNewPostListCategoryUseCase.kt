package daily.dayo.domain.usecase.post

import daily.dayo.domain.model.Category
import daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestNewPostListCategoryUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(category: Category) =
        postRepository.requestNewPostListCategory(category)
}