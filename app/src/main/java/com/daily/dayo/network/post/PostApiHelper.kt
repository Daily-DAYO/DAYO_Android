package com.daily.dayo.network.post

import com.daily.dayo.post.model.*
import retrofit2.Response

interface PostApiHelper {
    suspend fun requestPostDetail(postId : Int) : Response<ResponsePost>
    suspend fun requestDeletePost(postId : Int) : Response<Void>
    suspend fun requestLikePost(request : RequestLikePost) : Response<ResponseLikePost>
    suspend fun requestUnlikePost(postId: Int) : Response<Void>
    suspend fun requestBookmarkPost(request : RequestBookmarkPost) : Response<ResponseBookmarkPost>
    suspend fun requestDeleteBookmarkPost(postId: Int) : Response<Void>
    suspend fun requestPostComment(postId : Int) : Response<ResponsePostComment>
    suspend fun requestCreatePostComment(request: RequestCreatePostComment) : Response<ResponseCreatePostComment>
    suspend fun requestDeletePostComment(commentId : Int) : Response<Void>
}