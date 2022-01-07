package com.daily.dayo.network.Post

import com.daily.dayo.post.model.ResponsePost
import retrofit2.Response

interface PostApiHelper {
    suspend fun requestPostDetail(postId : Int) : Response<ResponsePost>
}