package daily.dayo.domain.usecase.follow

import daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class RequestListAllFollowingUseCase @Inject constructor(
    private val followRepository: FollowRepository
) {
    suspend operator fun invoke(memberId: String) =
        followRepository.requestListAllFollowing(memberId)
}