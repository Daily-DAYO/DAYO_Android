package com.daily.dayo.network.follow

import com.daily.dayo.profile.model.RequestCreateFollow
import com.daily.dayo.profile.model.ResponseCreateFollow
import com.daily.dayo.profile.model.ResponseListAllFollow
import retrofit2.Response

interface FollowApiHelper {

    suspend fun requestCreateFollow(body:RequestCreateFollow): Response<ResponseCreateFollow>
    suspend fun requestDeleteFollow(followerId: String): Response<Void>
    suspend fun requestListAllFollower(memberId: String): Response<ResponseListAllFollow>
    suspend fun requestListAllMyFollower(): Response<ResponseListAllFollow>
    suspend fun requestListAllFollowing(memberId: String) : Response<ResponseListAllFollow>
    suspend fun requestListAllMyFollowing(): Response<ResponseListAllFollow>
    suspend fun requestCreateFollowUp(body: RequestCreateFollow): Response<ResponseCreateFollow>

}