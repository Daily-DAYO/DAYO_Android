package com.daily.dayo.network.post

import com.daily.dayo.post.model.RequestCreatePostComment
import com.daily.dayo.post.model.ResponseCreatePostComment
import com.daily.dayo.post.model.ResponsePost
import com.daily.dayo.post.model.ResponsePostComment
import retrofit2.Response

interface PostApiHelper {
    suspend fun requestPostDetail(postId : Int) : Response<ResponsePost>
    suspend fun requestDeletePost(postId : Int) : Response<Void>
    suspend fun requestPostComment(postId : Int) : Response<ResponsePostComment>
    suspend fun requestCreatePostComment(request: RequestCreatePostComment) : Response<ResponseCreatePostComment>
    suspend fun requestDeletePostComment(commentId : Int) : Response<Void>
}