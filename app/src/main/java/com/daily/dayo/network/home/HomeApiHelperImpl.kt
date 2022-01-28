package com.daily.dayo.network.home

import com.daily.dayo.home.model.ResponseHomePost
import retrofit2.Response
import javax.inject.Inject

class HomeApiHelperImpl @Inject constructor(private val homeApiService: HomeApiService) : HomeApiHelper {
    // @Inject constructor을 통해 생성자에 Retrofit2 Interface 주입
    // ApiHelperImpl will be the class which will implement ApiHelper to provide functionality to ApiHelper functions.

    // In ApiHelperImpl we have injected ApiService in the constructor itself
    // So we do not have to create new instance of ApiService instead it is passed as a dependency to the ApiHelperImpl.
    // In this way we can have only one instance of ApiService throughout the application lifecycle for any number of network calls.
    override suspend fun requestPostList(): Response<ResponseHomePost> = homeApiService.requestPostList()
    override suspend fun requestPostListCategory(category: String): Response<ResponseHomePost> = homeApiService.requestPostListCategory(category)
}