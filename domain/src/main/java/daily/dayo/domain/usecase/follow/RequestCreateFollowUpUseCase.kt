package daily.dayo.domain.usecase.follow

import daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class RequestCreateFollowUpUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(followerId: String) =
        followRepository.requestCreateFollowUp(followerId = followerId)
}