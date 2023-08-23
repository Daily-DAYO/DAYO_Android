package daily.dayo.data.repository

import daily.dayo.data.datasource.remote.comment.CommentApiService
import daily.dayo.data.datasource.remote.comment.CreateCommentRequest
import daily.dayo.data.mapper.toComments
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.repository.CommentRepository
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