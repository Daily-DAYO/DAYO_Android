package com.daily.dayo.network.feed

import com.daily.dayo.feed.model.ResponseFeedList
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.post.model.ResponseLikePost
import retrofit2.Response

interface FeedApiHelper {
    suspend fun requestFeedList(): Response<ResponseFeedList>
    suspend fun requestLikePost(request : RequestLikePost) : Response<ResponseLikePost>
    suspend fun requestUnlikePost(postId: Int) : Response<Void>
}