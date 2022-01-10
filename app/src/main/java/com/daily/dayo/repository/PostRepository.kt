package com.daily.dayo.repository

import com.daily.dayo.network.Post.PostApiHelper
import com.daily.dayo.post.model.RequestCreatePostComment
import com.daily.dayo.post.model.ResponseCreatePostComment
import com.daily.dayo.post.model.ResponsePost
import com.daily.dayo.post.model.ResponsePostComment
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(private val postApiHelper: PostApiHelper) {
    suspend fun requestPostDetail(postId: Int) = postApiHelper.requestPostDetail(postId).verify()
    suspend fun requestPostComment(postId: Int) = postApiHelper.requestPostComment(postId).verify()
    suspend fun requestCreatePostComment(request: RequestCreatePostComment) = postApiHelper.requestCreatePostComment(request).verify()

    fun Response<ResponsePost>.verify() : Response<ResponsePost> {
        if (this.isSuccessful && this.code() in 200..299) {
            return this
        } else {
            throw Exception("${this.code()}")
        }
    }

    @JvmName("verifyResponsePostComment")
    fun Response<ResponsePostComment>.verify() : Response<ResponsePostComment> {
        if (this.isSuccessful && this.code() in 200..299) {
            return this
        } else {
            throw Exception("${this.code()}")
        }
    }

    @JvmName("verifyResponseCreatePostComment")
    fun Response<ResponseCreatePostComment>.verify() : Response<ResponseCreatePostComment> {
        if (this.isSuccessful && this.code() in 200..299) {
            return this
        } else {
            throw Exception("${this.code()}")
        }
    }
}