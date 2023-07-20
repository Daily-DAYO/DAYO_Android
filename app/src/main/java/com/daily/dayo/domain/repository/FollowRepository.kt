package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.follow.*
import com.daily.dayo.domain.model.NetworkResponse

interface FollowRepository {

    suspend fun requestCreateFollow(body: CreateFollowRequest): NetworkResponse<CreateFollowResponse>
    suspend fun requestDeleteFollow(followerId: String): NetworkResponse<Void>
    suspend fun requestListAllFollower(memberId: String): NetworkResponse<ListAllFollowerResponse>
    suspend fun requestListAllFollowing(memberId: String): NetworkResponse<ListAllFollowingResponse>
    suspend fun requestCreateFollowUp(body: CreateFollowUpRequest): NetworkResponse<CreateFollowUpResponse>
}