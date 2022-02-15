package com.daily.dayo.repository

import com.daily.dayo.feed.model.ResponseFeedList
import com.daily.dayo.network.feed.FeedApiHelper
import com.daily.dayo.post.model.RequestLikePost
import retrofit2.Response
import javax.inject.Inject

class FeedRepository@Inject constructor(private val feedApiHelper: FeedApiHelper){

    suspend fun requestFeedList(): Response<ResponseFeedList> = feedApiHelper.requestFeedList()
    suspend fun requestLikePost(request : RequestLikePost) = feedApiHelper.requestLikePost(request).verify()
    suspend fun requestUnlikePost(postId: Int) = feedApiHelper.requestUnlikePost(postId)

}