package com.daily.dayo.network.feed

import com.daily.dayo.feed.model.ResponseFeedList
import retrofit2.Response
import retrofit2.http.GET

interface FeedApiService {
    @GET("/api/v1/posts/feed/list")
    suspend fun requestFeedList(): Response<ResponseFeedList>
}