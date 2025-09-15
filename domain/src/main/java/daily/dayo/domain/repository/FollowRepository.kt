package daily.dayo.domain.repository

import daily.dayo.domain.model.FollowCreateResponse
import daily.dayo.domain.model.FollowUpCreateResponse
import daily.dayo.domain.model.Follower
import daily.dayo.domain.model.Following
import daily.dayo.domain.model.NetworkResponse

interface FollowRepository {

    suspend fun requestCreateFollow(followerId: String): NetworkResponse<FollowCreateResponse>
    suspend fun requestDeleteFollow(followerId: String): NetworkResponse<Void>
    suspend fun requestListAllFollower(memberId: String): NetworkResponse<Follower>
    suspend fun requestListAllFollowing(memberId: String): NetworkResponse<Following>
    suspend fun requestCreateFollowUp(followerId: String): NetworkResponse<FollowUpCreateResponse>
}