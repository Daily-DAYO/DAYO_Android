package com.daily.dayo.network.feed

import com.daily.dayo.feed.model.ResponseFeedList
import retrofit2.Response

interface FeedApiHelper {
    suspend fun requestFeedList(): Response<ResponseFeedList>
}