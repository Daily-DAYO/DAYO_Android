package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.comment.CreateCommentRequest
import com.daily.dayo.data.datasource.remote.comment.CreateCommentResponse
import com.daily.dayo.data.datasource.remote.comment.ListAllCommentResponse
import com.daily.dayo.domain.model.NetworkResponse

interface CommentRepository {

    suspend fun requestCreatePostComment(body: CreateCommentRequest): NetworkResponse<CreateCommentResponse>
    suspend fun requestPostComment(postId: Int): NetworkResponse<ListAllCommentResponse>
    suspend fun requestDeletePostComment(commentId: Int): NetworkResponse<Void>
}