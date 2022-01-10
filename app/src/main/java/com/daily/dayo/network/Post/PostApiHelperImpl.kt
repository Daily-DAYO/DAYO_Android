package com.daily.dayo.network.Post

import com.daily.dayo.post.model.RequestCreatePostComment
import com.daily.dayo.post.model.ResponseCreatePostComment
import com.daily.dayo.post.model.ResponsePost
import com.daily.dayo.post.model.ResponsePostComment
import retrofit2.Response
import javax.inject.Inject

class PostApiHelperImpl @Inject constructor(private val postApiService: PostApiService) : PostApiHelper {
    override suspend fun requestPostDetail(postId: Int): Response<ResponsePost> = postApiService.requestPostDetail(postId)
    override suspend fun requestPostComment(postId: Int): Response<ResponsePostComment> = postApiService.requestPostComment(postId)
    override suspend fun requestCreatePostComment(request: RequestCreatePostComment): Response<ResponseCreatePostComment> = postApiService.requestCreatePostComment(request)
}