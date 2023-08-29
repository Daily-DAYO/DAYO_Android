package daily.dayo.domain.repository

import daily.dayo.domain.model.FollowCreateResponse
import daily.dayo.domain.model.FollowUpCreateResponse
import daily.dayo.domain.model.Followers
import daily.dayo.domain.model.Followings
import daily.dayo.domain.model.NetworkResponse

interface FollowRepository {

    suspend fun requestCreateFollow(followerId: String): NetworkResponse<FollowCreateResponse>
    suspend fun requestDeleteFollow(followerId: String): NetworkResponse<Void>
    suspend fun requestListAllFollower(memberId: String): NetworkResponse<Followers>
    suspend fun requestListAllFollowing(memberId: String): NetworkResponse<Followings>
    suspend fun requestCreateFollowUp(followerId: String): NetworkResponse<FollowUpCreateResponse>
}