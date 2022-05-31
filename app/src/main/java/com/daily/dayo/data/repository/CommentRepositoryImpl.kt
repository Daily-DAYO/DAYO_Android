package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.comment.CommentApiService
import com.daily.dayo.data.datasource.remote.comment.CreateCommentRequest
import com.daily.dayo.data.datasource.remote.comment.CreateCommentResponse
import com.daily.dayo.data.datasource.remote.comment.ListAllCommentResponse
import com.daily.dayo.domain.repository.CommentRepository
import retrofit2.Response
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentApiService: CommentApiService
) : CommentRepository {

    override suspend fun requestPostComment(postId: Int): Response<ListAllCommentResponse> =
        commentApiService.requestPostComment(postId)

    override suspend fun requestCreatePostComment(body: CreateCommentRequest): Response<CreateCommentResponse> =
        commentApiService.requestCreatePostComment(body)

    override suspend fun requestDeletePostComment(commentId: Int): Response<Void> =
        commentApiService.requestDeletePostComment(commentId)
}