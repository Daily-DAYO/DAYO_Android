package com.daily.dayo.repository

import com.daily.dayo.network.Post.PostApiHelper
import com.daily.dayo.post.model.ResponsePost
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(private val postApiHelper: PostApiHelper) {
    suspend fun requestPostDetail(postId: Int) = postApiHelper.requestPostDetail(postId).verify()

    fun Response<ResponsePost>.verify() : Response<ResponsePost> {
        if (this.isSuccessful && this.code() in 200..299) {
            return this
        } else {
            throw Exception("${this.code()}")
        }
    }
}