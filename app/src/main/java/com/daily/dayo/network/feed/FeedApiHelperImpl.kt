package com.daily.dayo.network.feed

import com.daily.dayo.feed.model.ResponseFeedList
import retrofit2.Response
import javax.inject.Inject

class FeedApiHelperImpl @Inject constructor(private val feedApiService: FeedApiService) : FeedApiHelper {
    override suspend fun requestFeedList(): Response<ResponseFeedList> =
        feedApiService.requestFeedList()
}