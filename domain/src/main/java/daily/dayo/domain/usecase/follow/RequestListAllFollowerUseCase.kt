package daily.dayo.domain.usecase.follow

import daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class RequestListAllFollowerUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(memberId: String) =
        followRepository.requestListAllFollower(memberId)
}