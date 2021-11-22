package com.daily.dayo.network.home

import com.daily.dayo.home.model.ResponseHomePost

interface HomeApiHelper {
    suspend fun requestPostList(): ResponseHomePost
}