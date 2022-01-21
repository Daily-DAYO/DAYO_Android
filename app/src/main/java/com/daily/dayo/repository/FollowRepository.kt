package com.daily.dayo.repository

import com.daily.dayo.network.follow.FollowApiHelper
import com.daily.dayo.profile.model.RequestCreateFollow
import com.daily.dayo.profile.model.ResponseCreateFollow
import com.daily.dayo.profile.model.ResponseListAllFollow
import retrofit2.Response
import javax.inject.Inject

class FollowRepository @Inject constructor(private val followApiHelper: FollowApiHelper){

    suspend fun requestListAllFollower(memberId: String): Response<ResponseListAllFollow> = followApiHelper.requestListAllFollower(memberId)

    suspend fun requestListAllFollowing(memberId: String): Response<ResponseListAllFollow> = followApiHelper.requestListAllFollowing(memberId)

    suspend fun requestCreateFollow(followerId: String): Response<ResponseCreateFollow> = followApiHelper.requestCreateFollow(RequestCreateFollow(followerId))

    suspend fun requestDeleteFollow(followerId: String): Response<Void> = followApiHelper.requestDeleteFollow(followerId)
}