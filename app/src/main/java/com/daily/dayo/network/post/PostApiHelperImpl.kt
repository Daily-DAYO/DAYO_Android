package com.daily.dayo.network.post

import com.daily.dayo.post.model.*
import retrofit2.Response
import javax.inject.Inject

class PostApiHelperImpl @Inject constructor(private val postApiService: PostApiService) : PostApiHelper {
    override suspend fun requestPostDetail(postId: Int): Response<ResponsePost> = postApiService.requestPostDetail(postId)
    override suspend fun requestDeletePost(postId: Int): Response<Void> = postApiService.requestDeletePost(postId)
    override suspend fun requestLikePost(request: RequestLikePost): Response<ResponseLikePost> = postApiService.requestLikePost(request)
    override suspend fun requestUnlikePost(postId: Int): Response<Void> = postApiService.requestUnlikePost(postId)
    override suspend fun requestBookmarkPost(request: RequestBookmarkPost): Response<ResponseBookmarkPost> = postApiService.requestBookmarkPost(request)
    override suspend fun requestDeleteBookmarkPost(postId: Int): Response<Void> = postApiService.requestDeleteBookmarkPost(postId)
    override suspend fun requestPostComment(postId: Int): Response<ResponsePostComment> = postApiService.requestPostComment(postId)
    override suspend fun requestCreatePostComment(request: RequestCreatePostComment): Response<ResponseCreatePostComment> = postApiService.requestCreatePostComment(request)
    override suspend fun requestDeletePostComment(commentId: Int): Response<Void> = postApiService.requestDeletePostComment(commentId)
}