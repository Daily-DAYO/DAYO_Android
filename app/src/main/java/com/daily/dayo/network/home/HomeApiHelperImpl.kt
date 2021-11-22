package com.daily.dayo.network.home

import com.daily.dayo.home.model.ResponseHomePost
import javax.inject.Inject

class HomeApiHelperImpl @Inject constructor(private val homeApiService: HomeApiService) : HomeApiService {
    override suspend fun requestPostList(): ResponseHomePost = homeApiService.requestPostList()
}