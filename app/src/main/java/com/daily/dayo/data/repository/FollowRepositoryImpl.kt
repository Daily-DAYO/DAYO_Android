package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.follow.*
import com.daily.dayo.domain.repository.FollowRepository
import retrofit2.Response
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followApiService: FollowApiService
) : FollowRepository {

    override suspend fun requestCreateFollow(body: CreateFollowRequest): Response<CreateFollowResponse> =
        followApiService.requestCreateFollow(body)

    override suspend fun requestDeleteFollow(followerId: String): Response<Void> =
        followApiService.requestDeleteFollow(followerId)

    override suspend fun requestListAllFollower(memberId: String): Response<ListAllFollowerResponse> =
        followApiService.requestListAllFollower(memberId)

    override suspend fun requestListAllFollowing(memberId: String): Response<ListAllFollowingResponse> =
        followApiService.requestListAllFollowing(memberId)

    override suspend fun requestCreateFollowUp(body: CreateFollowUpRequest): Response<CreateFollowUpResponse> =
        followApiService.requestCreateFollowUp(body)

}