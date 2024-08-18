package daily.dayo.data.repository

import daily.dayo.data.datasource.remote.comment.CommentApiService
import daily.dayo.data.datasource.remote.comment.CreateCommentReplyRequest
import daily.dayo.data.datasource.remote.comment.CreateCommentRequest
import daily.dayo.data.datasource.remote.comment.MentionUserDto
import daily.dayo.data.mapper.toComments
import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.MentionUser
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
        postId: Int,
        mentionList: List<MentionUser>
    ): NetworkResponse<Int> =
        when (val response =
            commentApiService.requestCreatePostComment(
                CreateCommentRequest(
                    contents = contents,
                    postId = postId,
                    mentionList = mentionList.map {
                        MentionUserDto(memberId = it.memberId, nickname = it.nickname)
                    }
                )
            )) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.commentId)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestCreatePostCommentReply(
        commentId: Long,
        contents: String,
        postId: Int,
        mentionList: List<MentionUser>
    ): NetworkResponse<Int> =
        when (val response =
            commentApiService.requestCreatePostCommentReply(
                CreateCommentReplyRequest(
                    commentId = commentId,
                    contents = contents,
                    postId = postId,
                    mentionList = mentionList.map {
                        MentionUserDto(memberId = it.memberId, nickname = it.nickname)
                    }
                )
            )) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.commentId)
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDeletePostComment(commentId: Long): NetworkResponse<Void> =
        commentApiService.requestDeletePostComment(commentId)
}