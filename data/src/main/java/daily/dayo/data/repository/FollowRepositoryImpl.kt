package daily.dayo.data.repository

import daily.dayo.data.datasource.remote.follow.*
import daily.dayo.data.mapper.toFollowCreateResponse
import daily.dayo.data.mapper.toFollowUpCreateResponse
import daily.dayo.data.mapper.toFollowers
import daily.dayo.data.mapper.toFollowings
import daily.dayo.domain.model.FollowCreateResponse
import daily.dayo.domain.model.FollowUpCreateResponse
import daily.dayo.domain.model.Followers
import daily.dayo.domain.model.Followings
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followApiService: FollowApiService
) : FollowRepository {

    override suspend fun requestCreateFollow(followerId: String): NetworkResponse<FollowCreateResponse> =
       when (val response = followApiService.requestCreateFollow(CreateFollowRequest(followerId = followerId))) {
           is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFollowCreateResponse())
           is NetworkResponse.NetworkError -> response
           is NetworkResponse.ApiError -> response
           is NetworkResponse.UnknownError -> response
       }

    override suspend fun requestDeleteFollow(followerId: String): NetworkResponse<Void> =
        followApiService.requestDeleteFollow(followerId)

    override suspend fun requestListAllFollower(memberId: String): NetworkResponse<Followers> =
        when (val response = followApiService.requestListAllFollower(memberId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFollowers())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestListAllFollowing(memberId: String): NetworkResponse<Followings> =
        when (val response = followApiService.requestListAllFollowing(memberId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFollowings())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestCreateFollowUp(followerId: String): NetworkResponse<FollowUpCreateResponse> =
        when (val response = followApiService.requestCreateFollowUp(CreateFollowUpRequest(followerId = followerId))) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toFollowUpCreateResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }
}