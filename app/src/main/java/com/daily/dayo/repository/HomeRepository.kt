package com.daily.dayo.repository

import com.daily.dayo.home.model.ResponseHomePost
import com.daily.dayo.network.home.HomeApiHelper
import com.daily.dayo.network.home.HomeApiHelperImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiHelper: HomeApiHelper) {
    suspend fun requestPostList() = homeApiHelper.requestPostList().verify()
}

fun Response<ResponseHomePost>.verify() : Response<ResponseHomePost> {
    if (this.isSuccessful && this.code() in 200..299) {
        return this
    } else {
        throw Exception("${this.code()}")
    }
}