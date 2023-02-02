package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.follow.*
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.FollowRepository
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followApiService: FollowApiService
) : FollowRepository {

    override suspend fun requestCreateFollow(body: CreateFollowRequest): NetworkResponse<CreateFollowResponse> =
        followApiService.requestCreateFollow(body)

    override suspend fun requestDeleteFollow(followerId: String): NetworkResponse<Void> =
        followApiService.requestDeleteFollow(followerId)

    override suspend fun requestListAllFollower(memberId: String): NetworkResponse<ListAllFollowerResponse> =
        followApiService.requestListAllFollower(memberId)

    override suspend fun requestListAllFollowing(memberId: String): NetworkResponse<ListAllFollowingResponse> =
        followApiService.requestListAllFollowing(memberId)

    override suspend fun requestCreateFollowUp(body: CreateFollowUpRequest): NetworkResponse<CreateFollowUpResponse> =
        followApiService.requestCreateFollowUp(body)

}