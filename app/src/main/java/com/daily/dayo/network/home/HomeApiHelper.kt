package com.daily.dayo.network.home

import com.daily.dayo.home.model.ResponseHomePost
import retrofit2.Response

interface HomeApiHelper {
    // ApiHelper는 Repository를 통해 ApiService에 접근할 수 있도록 지원
    // (ApiHelper will help ApiService to be accessed via repository maintaining encapsulation.)
    suspend fun requestPostList(): Response<ResponseHomePost>
    suspend fun requestPostListCategory(category: String): Response<ResponseHomePost>
}