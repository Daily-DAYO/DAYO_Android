package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.comment.CreateCommentRequest
import com.daily.dayo.data.datasource.remote.comment.CreateCommentResponse
import com.daily.dayo.data.datasource.remote.comment.ListAllCommentResponse
import retrofit2.Response

interface CommentRepository {

    suspend fun requestCreatePostComment(body: CreateCommentRequest): Response<CreateCommentResponse>
    suspend fun requestPostComment(postId: Int): Response<ListAllCommentResponse>
    suspend fun requestDeletePostComment(commentId: Int): Response<Void>
}