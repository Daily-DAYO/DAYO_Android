package com.daily.dayo.repository

import com.daily.dayo.home.model.RequestLikePost
import com.daily.dayo.home.model.ResponseHomePost
import com.daily.dayo.home.model.ResponseLikePost
import com.daily.dayo.network.home.HomeApiHelper
import com.daily.dayo.network.home.HomeApiHelperImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiHelper: HomeApiHelper) {
    suspend fun requestPostList() = homeApiHelper.requestPostList().verify()
    suspend fun requestPostListCategory(category: String) = homeApiHelper.requestPostListCategory(category).verify()
    suspend fun requestLikePost(request : RequestLikePost) = homeApiHelper.requestLikePost(request).verify()
    suspend fun requestUnlikePost(postId: Int) = homeApiHelper.requestUnlikePost(postId)
}

fun Response<ResponseHomePost>.verify() : Response<ResponseHomePost> {
    if (this.isSuccessful && this.code() in 200..299) {
        return this
    } else {
        throw Exception("${this.code()}")
    }
}

@JvmName("verifyResponseLikePost")
fun Response<ResponseLikePost>.verify() : Response<ResponseLikePost> {
    if (this.isSuccessful && this.code() in 200..299) {
        return this
    } else {
        throw Exception("${this.code()}")
    }
}