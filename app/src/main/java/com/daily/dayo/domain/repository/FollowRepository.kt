package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.follow.*
import retrofit2.Response

interface FollowRepository {

    suspend fun requestCreateFollow(body: CreateFollowRequest): Response<CreateFollowResponse>
    suspend fun requestDeleteFollow(followerId: String): Response<Void>
    suspend fun requestListAllFollower(memberId: String): Response<ListAllFollowerResponse>
    suspend fun requestListAllFollowing(memberId: String): Response<ListAllFollowingResponse>
    suspend fun requestCreateFollowUp(body: CreateFollowUpRequest): Response<CreateFollowUpResponse>
}