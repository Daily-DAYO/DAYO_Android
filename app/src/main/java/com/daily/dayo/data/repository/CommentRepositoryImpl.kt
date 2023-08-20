package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.comment.CommentApiService
import com.daily.dayo.data.datasource.remote.comment.CreateCommentRequest
import com.daily.dayo.data.mapper.toComments
import com.daily.dayo.domain.model.Comments
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentApiService: CommentApiService
) : CommentRepository {

    override suspend fun requestPostComment(postId: Int): NetworkResponse<Comments> =
        when (val response =
            commentApiService.requestPostComment(postId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toComments())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestCreatePostComment(
        contents: String,
        postId: Int
    ): NetworkResponse<Int> =
        when (val response =
            commentApiService.requestCreatePostComment(
                CreateCommentRequest(contents = contents, postId = postId)
            )) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.commentId)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDeletePostComment(commentId: Int): NetworkResponse<Void> =
        commentApiService.requestDeletePostComment(commentId)
}