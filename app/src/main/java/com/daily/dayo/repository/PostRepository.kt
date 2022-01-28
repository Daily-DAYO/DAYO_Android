package com.daily.dayo.repository

import com.daily.dayo.network.post.PostApiHelper
import com.daily.dayo.post.model.*
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(private val postApiHelper: PostApiHelper) {
    suspend fun requestPostDetail(postId: Int) = postApiHelper.requestPostDetail(postId).verify()
    suspend fun requestDeletePost(postId: Int) = postApiHelper.requestDeletePost(postId)
    suspend fun requestLikePost(request : RequestLikePost) = postApiHelper.requestLikePost(request).verify()
    suspend fun requestUnlikePost(postId: Int) = postApiHelper.requestUnlikePost(postId)
    suspend fun requestPostComment(postId: Int) = postApiHelper.requestPostComment(postId).verify()
    suspend fun requestCreatePostComment(request: RequestCreatePostComment) = postApiHelper.requestCreatePostComment(request).verify()
    suspend fun requestDeletePostComment(commentId: Int) = postApiHelper.requestDeletePostComment(commentId)

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

    @JvmName("verifyResponseLikePost")
    fun Response<ResponseLikePost>.verify() : Response<ResponseLikePost> {
        if (this.isSuccessful && this.code() in 200..299) {
            return this
        } else {
            throw Exception("${this.code()}")
        }
    }
}