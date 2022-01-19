package com.daily.dayo.network.follow

import com.daily.dayo.profile.model.ResponseCreateFollow
import com.daily.dayo.profile.model.ResponseListAllFollow
import retrofit2.Response
import javax.inject.Inject

class FollowApiHelperImpl @Inject constructor(private val followApiService: FollowApiService) : FollowApiHelper {
    override suspend fun requestCreateFollow(followerId: String): Response<ResponseCreateFollow> =
        followApiService.requestCreateFollow(followerId)

    override suspend fun requestDeleteFollow(followerId: String): Response<Void> =
        followApiService.requestDeleteFollow(followerId)

    override suspend fun requestListAllFollower(memberId: String): Response<ResponseListAllFollow> =
        followApiService.requestListAllFollower(memberId)

    override suspend fun requestListAllMyFollower(): Response<ResponseListAllFollow> =
        followApiService.requestListAllMyFollower()

    override suspend fun requestListAllFollowing(memberId: String): Response<ResponseListAllFollow> =
        followApiService.requestListAllFollowing(memberId)

    override suspend fun requestListAllMyFollowing(): Response<ResponseListAllFollow> =
        followApiService.requestListAllMyFollowing()

    override suspend fun requestCreateFollowUp(followerId: String): Response<ResponseCreateFollow> =
        followApiService.requestCreateFollowUp(followerId)
}