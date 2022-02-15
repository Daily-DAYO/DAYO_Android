package com.daily.dayo.network.feed

import com.daily.dayo.feed.model.ResponseFeedList
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.post.model.ResponseLikePost
import retrofit2.Response
import javax.inject.Inject

class FeedApiHelperImpl @Inject constructor(private val feedApiService: FeedApiService) : FeedApiHelper {
    override suspend fun requestFeedList(): Response<ResponseFeedList> =
        feedApiService.requestFeedList()
    override suspend fun requestLikePost(request: RequestLikePost): Response<ResponseLikePost> =
        feedApiService.requestLikePost(request)
    override suspend fun requestUnlikePost(postId: Int): Response<Void> =
        feedApiService.requestUnlikePost(postId)
}