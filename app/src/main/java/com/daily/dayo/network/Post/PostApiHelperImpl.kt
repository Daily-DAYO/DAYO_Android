package com.daily.dayo.network.Post

import com.daily.dayo.post.model.ResponsePost
import retrofit2.Response
import javax.inject.Inject

class PostApiHelperImpl @Inject constructor(private val postApiService: PostApiService) : PostApiHelper {
    override suspend fun requestPostDetail(postId: Int): Response<ResponsePost> = postApiService.requestPostDetail(postId)
}