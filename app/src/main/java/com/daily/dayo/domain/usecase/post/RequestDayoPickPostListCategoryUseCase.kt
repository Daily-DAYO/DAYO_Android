package com.daily.dayo.domain.usecase.post

import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.repository.PostRepository
import javax.inject.Inject

class RequestDayoPickPostListCategoryUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(category: Category) =
        postRepository.requestDayoPickPostListCategory(category)
}