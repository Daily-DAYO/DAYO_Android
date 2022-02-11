package com.daily.dayo.repository

import com.daily.dayo.feed.model.ResponseFeedList
import com.daily.dayo.network.feed.FeedApiHelper
import retrofit2.Response
import javax.inject.Inject

class FeedRepository@Inject constructor(private val feedApiHelper: FeedApiHelper){

    suspend fun requestFeedList(): Response<ResponseFeedList> = feedApiHelper.requestFeedList()
}