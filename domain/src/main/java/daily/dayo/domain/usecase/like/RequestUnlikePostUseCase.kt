package daily.dayo.domain.usecase.like

import daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class RequestUnlikePostUseCase @Inject constructor(
    private val heartRepository: HeartRepository
) {
    suspend operator fun invoke(postId: Long) =
        heartRepository.requestUnlikePost(postId)
}