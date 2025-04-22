package daily.dayo.domain.usecase.like

import daily.dayo.domain.repository.HeartRepository
import javax.inject.Inject

class RequestPostLikeUsersUseCase @Inject constructor(
    private val heartRepository: HeartRepository
) {
    suspend operator fun invoke(postId: Long) =
        heartRepository.requestPostLikeUsers(postId = postId)
}