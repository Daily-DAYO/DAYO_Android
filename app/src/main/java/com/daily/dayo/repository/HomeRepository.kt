package com.daily.dayo.repository

import com.daily.dayo.home.model.ResponseHomePost
import com.daily.dayo.network.home.HomeApiHelper
import com.daily.dayo.network.post.PostApiHelper
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.post.model.ResponseLikePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiHelper: HomeApiHelper, private val postApiHelper: PostApiHelper) {
    suspend fun requestPostList() = homeApiHelper.requestPostList().verify()
    suspend fun requestPostListCategory(category: String) = homeApiHelper.requestPostListCategory(category).verify()
    suspend fun requestLikePost(request : RequestLikePost) = postApiHelper.requestLikePost(request).verify()
    suspend fun requestUnlikePost(postId: Int) = postApiHelper.requestUnlikePost(postId)
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